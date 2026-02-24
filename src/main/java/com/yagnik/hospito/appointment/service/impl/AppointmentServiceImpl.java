package com.yagnik.hospito.appointment.service.impl;

import com.yagnik.hospito.appointment.dto.*;
import com.yagnik.hospito.appointment.entity.Appointment;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import com.yagnik.hospito.appointment.repository.AppointmentRepository;
import com.yagnik.hospito.appointment.service.AppointmentService;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.branch.repository.BranchRepository;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import com.yagnik.hospito.doctor.entity.Doctor;
import com.yagnik.hospito.doctor.repository.DoctorRepository;
import com.yagnik.hospito.patient.entity.Patient;
import com.yagnik.hospito.patient.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {

        // Validate patient exists and is active
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Patient", request.getPatientId()));

        if (!patient.isActive()) {
            throw new BusinessRuleException("Patient account is inactive.");
        }

        // Validate doctor exists and is active
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Doctor", request.getDoctorId()));

        if (!doctor.isActive()) {
            throw new BusinessRuleException("Doctor is not active.");
        }

        // Validate branch exists and is active
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Branch", request.getBranchId()));

        if (!branch.isActive()) {
            throw new BusinessRuleException("Branch is not active.");
        }

        // Validate doctor works at this branch
        boolean doctorInBranch = doctor.getBranches().stream()
                .anyMatch(b -> b.getId().equals(request.getBranchId()));

        if (!doctorInBranch) {
            throw new BusinessRuleException(
                "Doctor is not assigned to this branch.");
        }

        // Conflict detection — prevent double booking
        List<AppointmentStatus> excludedStatuses = List.of(
            AppointmentStatus.CANCELLED,
            AppointmentStatus.NO_SHOW);

        boolean hasConflict = appointmentRepository.existsConflict(
                request.getDoctorId(),
                request.getAppointmentTime(),
                excludedStatuses);

        if (hasConflict) {
            throw new BusinessRuleException(
                "Doctor already has an appointment at this time. Please choose a different slot.");
        }

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .branch(branch)
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        // Add branch to patient's visited branches
        patient.getVisitedBranches().add(branch);
        patientRepository.save(patient);

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        return mapToResponse(findAppointmentById(id));
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByBranch(
            Long branchId, AppointmentStatus status) {

        branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", branchId));

        List<Appointment> appointments;
        if (status != null) {
            appointments = appointmentRepository
                .findAllByBranchIdAndStatusOrderByAppointmentTimeDesc(branchId, status);
        } else {
            appointments = appointmentRepository
                .findAllByBranchIdOrderByAppointmentTimeDesc(branchId);
        }

        return appointments.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDoctor(
            Long doctorId, AppointmentStatus status) {

        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));

        List<Appointment> appointments;
        if (status != null) {
            appointments = appointmentRepository
                .findAllByDoctorIdAndStatusOrderByAppointmentTimeDesc(doctorId, status);
        } else {
            appointments = appointmentRepository
                .findAllByDoctorIdOrderByAppointmentTimeDesc(doctorId);
        }

        return appointments.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));

        return appointmentRepository
                .findAllByPatientIdOrderByAppointmentTimeDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(
            Long id, UpdateAppointmentStatusRequest request) {

        Appointment appointment = findAppointmentById(id);

        // Validate status transition
        validateStatusTransition(appointment.getStatus(), request.getStatus());

        appointment.setStatus(request.getStatus());

        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = findAppointmentById(id);

        // Can only cancel PENDING or CONFIRMED
        if (appointment.getStatus() == AppointmentStatus.COMPLETED ||
            appointment.getStatus() == AppointmentStatus.NO_SHOW ||
            appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException(
                "Cannot cancel an appointment with status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private void validateStatusTransition(
            AppointmentStatus current, AppointmentStatus next) {

        boolean valid = switch (current) {
            case PENDING -> next == AppointmentStatus.CONFIRMED ||
                           next == AppointmentStatus.CANCELLED;
            case CONFIRMED -> next == AppointmentStatus.IN_PROGRESS ||
                             next == AppointmentStatus.CANCELLED ||
                             next == AppointmentStatus.NO_SHOW;
            case IN_PROGRESS -> next == AppointmentStatus.COMPLETED;
            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };

        if (!valid) {
            throw new BusinessRuleException(
                "Invalid status transition from " + current + " to " + next);
        }
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .doctorSpecialization(appointment.getDoctor().getSpecialization())
                .branchId(appointment.getBranch().getId())
                .branchName(appointment.getBranch().getName())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}