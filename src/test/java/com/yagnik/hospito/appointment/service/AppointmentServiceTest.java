package com.yagnik.hospito.appointment.service;

import com.yagnik.hospito.appointment.dto.CreateAppointmentRequest;
import com.yagnik.hospito.appointment.dto.AppointmentResponse;
import com.yagnik.hospito.appointment.dto.UpdateAppointmentStatusRequest;
import com.yagnik.hospito.appointment.entity.Appointment;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import com.yagnik.hospito.appointment.repository.AppointmentRepository;
import com.yagnik.hospito.appointment.service.impl.AppointmentServiceImpl;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.branch.repository.BranchRepository;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import com.yagnik.hospito.doctor.entity.Doctor;
import com.yagnik.hospito.doctor.repository.DoctorRepository;
import com.yagnik.hospito.patient.entity.Patient;
import com.yagnik.hospito.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Unit Tests")
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private BranchRepository branchRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Branch mockBranch;
    private Doctor mockDoctor;
    private Patient mockPatient;
    private Appointment mockAppointment;
    private CreateAppointmentRequest createRequest;
    private LocalDateTime futureTime;

    @BeforeEach
    void setUp() {
        futureTime = LocalDateTime.now().plusDays(7);

        mockBranch = Branch.builder()
                .id(1L).name("Hospito Surat").isActive(true)
                .build();

        mockDoctor = Doctor.builder()
                .id(1L).name("Dr. Aanya Sharma")
                .specialization("Cardiologist")
                .isActive(true)
                .branches(new HashSet<>(Set.of(mockBranch)))
                .build();

        mockPatient = Patient.builder()
                .id(1L).name("Rahul Mehta")
                .isActive(true)
                .visitedBranches(new HashSet<>())
                .build();

        mockAppointment = Appointment.builder()
                .id(1L)
                .appointmentTime(futureTime)
                .reason("Chest pain")
                .status(AppointmentStatus.PENDING)
                .patient(mockPatient)
                .doctor(mockDoctor)
                .branch(mockBranch)
                .build();

        createRequest = new CreateAppointmentRequest();
        createRequest.setPatientId(1L);
        createRequest.setDoctorId(1L);
        createRequest.setBranchId(1L);
        createRequest.setAppointmentTime(futureTime);
        createRequest.setReason("Chest pain");
    }

    // ── CREATE APPOINTMENT ───────────────────────────────────────────────────

    @Test
    @DisplayName("createAppointment - valid data - should return AppointmentResponse")
    void createAppointment_withValidData_shouldReturnAppointmentResponse() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(appointmentRepository.existsConflict(anyLong(), any(), anyList())).thenReturn(false);
        when(patientRepository.save(any())).thenReturn(mockPatient);
        when(appointmentRepository.save(any())).thenReturn(mockAppointment);

        // Act
        AppointmentResponse response = appointmentService.createAppointment(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        assertThat(response.getPatientName()).isEqualTo("Rahul Mehta");
        assertThat(response.getDoctorName()).isEqualTo("Dr. Aanya Sharma");
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createAppointment - conflict detected - should throw BusinessRuleException")
    void createAppointment_withConflict_shouldThrowBusinessRuleException() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(appointmentRepository.existsConflict(anyLong(), any(), anyList())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> appointmentService.createAppointment(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already has an appointment at this time");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("createAppointment - inactive doctor - should throw BusinessRuleException")
    void createAppointment_withInactiveDoctor_shouldThrowBusinessRuleException() {
        // Arrange
        mockDoctor.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));

        // Act + Assert
        assertThatThrownBy(() -> appointmentService.createAppointment(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not active");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("createAppointment - doctor not in branch - should throw BusinessRuleException")
    void createAppointment_withDoctorNotInBranch_shouldThrowBusinessRuleException() {
        // Arrange — doctor has no branches
        mockDoctor.setBranches(new HashSet<>());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));

        // Act + Assert
        assertThatThrownBy(() -> appointmentService.createAppointment(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not assigned to this branch");

        verify(appointmentRepository, never()).save(any());
    }

    // ── UPDATE STATUS ────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStatus - PENDING to CONFIRMED - should update status")
    void updateStatus_pendingToConfirmed_shouldUpdateStatus() {
        // Arrange
        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest();
        request.setStatus(AppointmentStatus.CONFIRMED);
        request.setNotes("Confirmed by admin");

        Appointment confirmed = Appointment.builder()
                .id(1L).status(AppointmentStatus.CONFIRMED)
                .reason("Chest pain").appointmentTime(futureTime)
                .patient(mockPatient).doctor(mockDoctor).branch(mockBranch)
                .notes("Confirmed by admin").build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentRepository.save(any())).thenReturn(confirmed);

        // Act
        AppointmentResponse response = appointmentService.updateStatus(1L, request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(response.getNotes()).isEqualTo("Confirmed by admin");
    }

    @Test
    @DisplayName("updateStatus - COMPLETED to PENDING - should throw BusinessRuleException")
    void updateStatus_completedToPending_shouldThrowBusinessRuleException() {
        // Arrange
        mockAppointment.setStatus(AppointmentStatus.COMPLETED);
        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest();
        request.setStatus(AppointmentStatus.PENDING);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));

        // Act + Assert
        assertThatThrownBy(() -> appointmentService.updateStatus(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Invalid status transition");
    }

    // ── CANCEL APPOINTMENT ───────────────────────────────────────────────────

    @Test
    @DisplayName("cancelAppointment - PENDING status - should cancel successfully")
    void cancelAppointment_withPendingStatus_shouldCancelSuccessfully() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(appointmentRepository.save(any())).thenReturn(mockAppointment);

        // Act
        appointmentService.cancelAppointment(1L);

        // Assert
        verify(appointmentRepository, times(1)).save(any());
        assertThat(mockAppointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancelAppointment - COMPLETED status - should throw BusinessRuleException")
    void cancelAppointment_withCompletedStatus_shouldThrowBusinessRuleException() {
        // Arrange
        mockAppointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));

        // Act + Assert
        assertThatThrownBy(() -> appointmentService.cancelAppointment(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot cancel");

        verify(appointmentRepository, never()).save(any());
    }
}