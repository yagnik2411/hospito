package com.yagnik.hospito.doctor.service;

import com.yagnik.hospito.doctor.dto.*;

import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(CreateDoctorRequest request);

    DoctorResponse getDoctorById(Long id);

    List<DoctorResponse> getDoctorsByBranch(Long branchId, String specialization);

    DoctorResponse updateDoctor(Long id, CreateDoctorRequest request);

    DoctorResponse transferDoctor(Long id, TransferDoctorRequest request);

    void deleteDoctor(Long id);

    List<DoctorAvailabilityResponse> setAvailability(Long id, List<DoctorAvailabilityRequest> request);

    List<DoctorAvailabilityResponse> getAvailability(Long id);
}