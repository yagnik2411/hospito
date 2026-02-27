package com.yagnik.hospito.doctor.service;

import com.yagnik.hospito.auth.entity.Role;
import com.yagnik.hospito.auth.entity.User;
import com.yagnik.hospito.auth.enums.RoleType;
import com.yagnik.hospito.auth.repository.RoleRepository;
import com.yagnik.hospito.auth.repository.UserRepository;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.branch.repository.BranchRepository;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import com.yagnik.hospito.doctor.dto.CreateDoctorRequest;
import com.yagnik.hospito.doctor.dto.DoctorResponse;
import com.yagnik.hospito.doctor.dto.TransferDoctorRequest;
import com.yagnik.hospito.doctor.entity.Doctor;
import com.yagnik.hospito.doctor.repository.DoctorAvailabilityRepository;
import com.yagnik.hospito.doctor.repository.DoctorRepository;
import com.yagnik.hospito.doctor.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService Unit Tests")
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private DoctorAvailabilityRepository availabilityRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Branch mockBranch;
    private Branch mockBranch2;
    private Role mockRole;
    private User mockUser;
    private Doctor mockDoctor;
    private CreateDoctorRequest createRequest;

    @BeforeEach
    void setUp() {
        mockBranch = Branch.builder()
                .id(1L).name("Hospito Surat").isActive(true)
                .build();

        mockBranch2 = Branch.builder()
                .id(2L).name("Hospito Mumbai").isActive(true)
                .build();

        mockRole = Role.builder()
                .id(1L).name(RoleType.DOCTOR)
                .build();

        mockUser = User.builder()
                .id(1L).name("Dr. Aanya Sharma")
                .email("aanya@hospito.com")
                .password("encoded_password")
                .role(mockRole).isActive(true)
                .build();

        mockDoctor = Doctor.builder()
                .id(1L).name("Dr. Aanya Sharma")
                .specialization("Cardiologist")
                .licenseNumber("MED-2024-001")
                .bio("15 years experience")
                .user(mockUser)
                .primaryBranch(mockBranch)
                .branches(new HashSet<>(Set.of(mockBranch)))
                .isActive(true)
                .build();

        createRequest = new CreateDoctorRequest();
        createRequest.setName("Dr. Aanya Sharma");
        createRequest.setSpecialization("Cardiologist");
        createRequest.setLicenseNumber("MED-2024-001");
        createRequest.setBio("15 years experience");
        createRequest.setEmail("aanya@hospito.com");
        createRequest.setPassword("doctor123");
        createRequest.setBranchId(1L);
    }

    // ── CREATE DOCTOR ────────────────────────────────────────────────────────

    @Test
    @DisplayName("createDoctor - valid data - should return DoctorResponse")
    void createDoctor_withValidData_shouldReturnDoctorResponse() {
        // Arrange
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(roleRepository.findByName(RoleType.DOCTOR)).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // Act
        DoctorResponse response = doctorService.createDoctor(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Dr. Aanya Sharma");
        assertThat(response.getSpecialization()).isEqualTo("Cardiologist");
        assertThat(response.getPrimaryBranchName()).isEqualTo("Hospito Surat");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("createDoctor - branch not found - should throw ResourceNotFoundException")
    void createDoctor_withNonExistentBranch_shouldThrowResourceNotFoundException() {
        // Arrange
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> doctorService.createDoctor(createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Branch");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    @DisplayName("createDoctor - inactive branch - should throw BusinessRuleException")
    void createDoctor_withInactiveBranch_shouldThrowBusinessRuleException() {
        // Arrange
        mockBranch.setActive(false);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));

        // Act + Assert
        assertThatThrownBy(() -> doctorService.createDoctor(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    @DisplayName("createDoctor - duplicate email - should throw BusinessRuleException")
    void createDoctor_withDuplicateEmail_shouldThrowBusinessRuleException() {
        // Arrange
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> doctorService.createDoctor(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Email already registered");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    @DisplayName("createDoctor - duplicate license number - should throw BusinessRuleException")
    void createDoctor_withDuplicateLicense_shouldThrowBusinessRuleException() {
        // Arrange
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber(anyString())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> doctorService.createDoctor(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("License number already registered");

        verify(doctorRepository, never()).save(any());
    }

    // ── GET DOCTOR ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getDoctorById - valid id - should return DoctorResponse")
    void getDoctorById_withValidId_shouldReturnDoctorResponse() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));

        // Act
        DoctorResponse response = doctorService.getDoctorById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Dr. Aanya Sharma");
        assertThat(response.getLicenseNumber()).isEqualTo("MED-2024-001");
    }

    @Test
    @DisplayName("getDoctorById - invalid id - should throw ResourceNotFoundException")
    void getDoctorById_withInvalidId_shouldThrowResourceNotFoundException() {
        // Arrange
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> doctorService.getDoctorById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── TRANSFER DOCTOR ──────────────────────────────────────────────────────

    @Test
    @DisplayName("transferDoctor - valid branch - should update primaryBranch")
    void transferDoctor_withValidBranch_shouldUpdatePrimaryBranch() {
        // Arrange
        TransferDoctorRequest request = new TransferDoctorRequest();
        request.setTargetBranchId(2L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        when(branchRepository.findById(2L)).thenReturn(Optional.of(mockBranch2));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // Act
        DoctorResponse response = doctorService.transferDoctor(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("transferDoctor - already in branch - should throw BusinessRuleException")
    void transferDoctor_toAlreadyAssignedBranch_shouldThrowBusinessRuleException() {
        // Arrange — mockDoctor already has mockBranch (id=1)
        TransferDoctorRequest request = new TransferDoctorRequest();
        request.setTargetBranchId(1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        when(branchRepository.findById(1L)).thenReturn(Optional.of(mockBranch));

        // Act + Assert
        assertThatThrownBy(() -> doctorService.transferDoctor(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already assigned");

        verify(doctorRepository, never()).save(any());
    }
}