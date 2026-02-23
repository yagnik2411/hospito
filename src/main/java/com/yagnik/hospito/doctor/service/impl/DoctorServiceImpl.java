package com.yagnik.hospito.doctor.service.impl;

import com.yagnik.hospito.auth.entity.Role;
import com.yagnik.hospito.auth.entity.User;
import com.yagnik.hospito.auth.enums.RoleType;
import com.yagnik.hospito.auth.repository.RoleRepository;
import com.yagnik.hospito.auth.repository.UserRepository;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.branch.repository.BranchRepository;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import com.yagnik.hospito.doctor.dto.*;
import com.yagnik.hospito.doctor.entity.Doctor;
import com.yagnik.hospito.doctor.entity.DoctorAvailability;
import com.yagnik.hospito.doctor.repository.DoctorAvailabilityRepository;
import com.yagnik.hospito.doctor.repository.DoctorRepository;
import com.yagnik.hospito.doctor.service.DoctorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest request) {

        // Step 1 — Branch must exist and be active
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch", request.getBranchId()));

        if (!branch.isActive()) {
            throw new BusinessRuleException(
                    "Cannot assign doctor to an inactive branch.");
        }

        // Step 2 — Email must not already exist
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException(
                    "Email already registered: " + request.getEmail());
        }

        // Step 3 — License number must be unique
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessRuleException(
                    "License number already registered: " + request.getLicenseNumber());
        }

        // Step 4 — Find DOCTOR role
        Role doctorRole = roleRepository.findByName(RoleType.DOCTOR)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DOCTOR role not found. Make sure roles are seeded."));

        // Step 5 — Create User account for doctor
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(doctorRole)
                .isActive(true)
                .build();
        userRepository.save(user);

        // Step 6 — Create Doctor entity
        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .bio(request.getBio())
                .primaryBranch(branch)
                .user(user)
                .build();

        // Step 7 — Add to branch's ManyToMany
        doctor.getBranches().add(branch);

        return mapToResponse(doctorRepository.save(doctor));
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        return mapToResponse(findDoctorById(id));
    }

    @Override
    public List<DoctorResponse> getDoctorsByBranch(Long branchId, String specialization) {
        // Verify branch exists
        branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        List<Doctor> doctors;
        if (specialization != null && !specialization.isBlank()) {
            doctors = doctorRepository.findByBranchIdAndSpecialization(
                    branchId, specialization);
        } else {
            doctors = doctorRepository.findActiveByBranchId(branchId);
        }

        return doctors.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, CreateDoctorRequest request) {
        Doctor doctor = findDoctorById(id);

        // Update only non-auth fields
        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setBio(request.getBio());

        // Update user name as well
        doctor.getUser().setName(request.getName());
        userRepository.save(doctor.getUser());

        return mapToResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public DoctorResponse transferDoctor(Long id, TransferDoctorRequest request) {
        Doctor doctor = findDoctorById(id);

        Branch targetBranch = branchRepository.findById(request.getTargetBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch", request.getTargetBranchId()));

        if (!targetBranch.isActive()) {
            throw new BusinessRuleException(
                    "Cannot transfer doctor to an inactive branch.");
        }

        // Check if already in target branch
        boolean alreadyInBranch = doctor.getBranches().stream()
                .anyMatch(b -> b.getId().equals(request.getTargetBranchId()));

        if (alreadyInBranch) {
            throw new BusinessRuleException(
                    "Doctor is already assigned to this branch.");
        }

        // Add to new branch + update primary branch
        doctor.getBranches().add(targetBranch);
        doctor.setPrimaryBranch(targetBranch);

        return mapToResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorById(id);
        doctor.setActive(false);
        doctor.getUser().setActive(false);
        userRepository.save(doctor.getUser());
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public List<DoctorAvailabilityResponse> setAvailability(
            Long id, List<DoctorAvailabilityRequest> requests) {

        Doctor doctor = findDoctorById(id);

        // Full replace strategy — delete all existing slots first
        availabilityRepository.deleteByDoctorId(id);

        // Save new slots
        List<DoctorAvailability> slots = requests.stream()
                .map(req -> DoctorAvailability.builder()
                        .dayOfWeek(req.getDayOfWeek())
                        .startTime(req.getStartTime())
                        .endTime(req.getEndTime())
                        .slotDurationMinutes(req.getSlotDurationMinutes())
                        .doctor(doctor)
                        .build())
                .collect(Collectors.toList());

        return availabilityRepository.saveAll(slots).stream()
                .map(this::mapAvailabilityToResponse)
                .toList();
    }

    @Override
    public List<DoctorAvailabilityResponse> getAvailability(Long id) {
        findDoctorById(id); // validates doctor exists
        return availabilityRepository.findByDoctorId(id).stream()
                .map(this::mapAvailabilityToResponse)
                .toList();
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .licenseNumber(doctor.getLicenseNumber())
                .bio(doctor.getBio())
                .isActive(doctor.isActive())
                .email(doctor.getUser().getEmail())
                .primaryBranchId(doctor.getPrimaryBranch().getId())
                .primaryBranchName(doctor.getPrimaryBranch().getName())
                .branchNames(doctor.getBranches().stream()
                        .map(Branch::getName)
                        .collect(Collectors.toSet()))
                .createdAt(doctor.getCreatedAt())
                .build();
    }

    private DoctorAvailabilityResponse mapAvailabilityToResponse(DoctorAvailability slot) {
        return DoctorAvailabilityResponse.builder()
                .id(slot.getId())
                .dayOfWeek(slot.getDayOfWeek())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .slotDurationMinutes(slot.getSlotDurationMinutes())
                .doctorId(slot.getDoctor().getId())
                .doctorName(slot.getDoctor().getName())
                .build();
    }
}