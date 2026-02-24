package com.yagnik.hospito.patient.service.impl;

import com.yagnik.hospito.auth.entity.Role;
import com.yagnik.hospito.auth.entity.User;
import com.yagnik.hospito.auth.enums.RoleType;
import com.yagnik.hospito.auth.repository.RoleRepository;
import com.yagnik.hospito.auth.repository.UserRepository;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.branch.repository.BranchRepository;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import com.yagnik.hospito.doctor.entity.Doctor;
import com.yagnik.hospito.doctor.repository.DoctorRepository;
import com.yagnik.hospito.patient.dto.*;
import com.yagnik.hospito.patient.entity.Insurance;
import com.yagnik.hospito.patient.entity.MedicalRecord;
import com.yagnik.hospito.patient.entity.Patient;
import com.yagnik.hospito.patient.repository.MedicalRecordRepository;
import com.yagnik.hospito.patient.repository.PatientRepository;
import com.yagnik.hospito.patient.service.PatientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {

        // Branch must exist and be active
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Branch", request.getBranchId()));

        if (!branch.isActive()) {
            throw new BusinessRuleException(
                "Cannot register patient at an inactive branch.");
        }

        // Email must not already exist
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException(
                "Email already registered: " + request.getEmail());
        }

        // Find PATIENT role
        Role patientRole = roleRepository.findByName(RoleType.PATIENT)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "PATIENT role not found. Make sure roles are seeded."));

        // Create User account
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(patientRole)
                .isActive(true)
                .build();
        userRepository.save(user);

        // Create Patient entity
        Patient patient = Patient.builder()
                .name(request.getName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .bloodGroup(request.getBloodGroup())
                .phone(request.getPhone())
                .address(request.getAddress())
                .user(user)
                .build();

        // Add to branch's visited patients
        patient.getVisitedBranches().add(branch);

        return mapToResponse(patientRepository.save(patient));
    }

    @Override
    public PatientResponse getPatientById(Long id) {
        return mapToResponse(findPatientById(id));
    }

    @Override
    public List<PatientResponse> getPatientsByBranch(Long branchId, String name) {
        branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        List<Patient> patients;
        if (name != null && !name.isBlank()) {
            patients = patientRepository.findByBranchIdAndNameContaining(branchId, name);
        } else {
            patients = patientRepository.findAllByBranchId(branchId);
        }

        return patients.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long id, CreatePatientRequest request) {
        Patient patient = findPatientById(id);

        patient.setName(request.getName());
        patient.setGender(request.getGender());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());

        // Update user name too
        patient.getUser().setName(request.getName());
        userRepository.save(patient.getUser());

        return mapToResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = findPatientById(id);
        patient.setActive(false);
        patient.getUser().setActive(false);
        userRepository.save(patient.getUser());
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public MedicalRecordResponse addMedicalRecord(
            Long patientId, MedicalRecordRequest request) {

        Patient patient = findPatientById(patientId);

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Doctor", request.getDoctorId()));

        MedicalRecord record = MedicalRecord.builder()
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription())
                .notes(request.getNotes())
                .recordDate(request.getRecordDate())
                .patient(patient)
                .doctor(doctor)
                .build();

        return mapRecordToResponse(medicalRecordRepository.save(record));
    }

    @Override
    public List<MedicalRecordResponse> getMedicalRecords(Long patientId) {
        findPatientById(patientId); // validate exists
        return medicalRecordRepository
                .findByPatientIdOrderByRecordDateDesc(patientId)
                .stream()
                .map(this::mapRecordToResponse)
                .toList();
    }

    @Override
    @Transactional
    public InsuranceResponse assignInsurance(Long patientId, InsuranceRequest request) {
        Patient patient = findPatientById(patientId);

        Insurance insurance = Insurance.builder()
                .policyNumber(request.getPolicyNumber())
                .provider(request.getProvider())
                .validTill(request.getValidTill())
                .build();

        patient.setInsurance(insurance);
        patientRepository.save(patient);

        return mapInsuranceToResponse(insurance, patient);
    }

    @Override
    public InsuranceResponse getInsurance(Long patientId) {
        Patient patient = findPatientById(patientId);

        if (patient.getInsurance() == null) {
            throw new ResourceNotFoundException(
                "No insurance found for patient with id " + patientId);
        }

        return mapInsuranceToResponse(patient.getInsurance(), patient);
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .bloodGroup(patient.getBloodGroup())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .isActive(patient.isActive())
                .email(patient.getUser().getEmail())
                .visitedBranchNames(patient.getVisitedBranches().stream()
                        .map(Branch::getName)
                        .collect(Collectors.toSet()))
                .createdAt(patient.getCreatedAt())
                .build();
    }

    private MedicalRecordResponse mapRecordToResponse(MedicalRecord record) {
        return MedicalRecordResponse.builder()
                .id(record.getId())
                .diagnosis(record.getDiagnosis())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .recordDate(record.getRecordDate())
                .patientId(record.getPatient().getId())
                .patientName(record.getPatient().getName())
                .doctorId(record.getDoctor().getId())
                .doctorName(record.getDoctor().getName())
                .build();
    }

    private InsuranceResponse mapInsuranceToResponse(Insurance insurance, Patient patient) {
        return InsuranceResponse.builder()
                .id(insurance.getId())
                .policyNumber(insurance.getPolicyNumber())
                .provider(insurance.getProvider())
                .validTill(insurance.getValidTill())
                .patientId(patient.getId())
                .patientName(patient.getName())
                .build();
    }
}