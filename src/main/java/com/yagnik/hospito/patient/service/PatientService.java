package com.yagnik.hospito.patient.service;

import com.yagnik.hospito.patient.dto.*;

import java.util.List;

public interface PatientService {
    PatientResponse createPatient(CreatePatientRequest request);
    PatientResponse getPatientById(Long id);
    List<PatientResponse> getPatientsByBranch(Long branchId, String name);
    PatientResponse updatePatient(Long id, CreatePatientRequest request);
    void deletePatient(Long id);
    MedicalRecordResponse addMedicalRecord(Long patientId, MedicalRecordRequest request);
    List<MedicalRecordResponse> getMedicalRecords(Long patientId);
    InsuranceResponse assignInsurance(Long patientId, InsuranceRequest request);
    InsuranceResponse getInsurance(Long patientId);
}