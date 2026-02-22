package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Insurance;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.InsuranceRepository;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceService {

  private final InsuranceRepository insuranceRepository;
  private final PatientRepository patientRepository;

  @Transactional
  public Patient assignInsuranceToPatient(Insurance insurance, Long patientId) {
    Patient patient = patientRepository.findById(patientId)
        .orElseThrow(() -> new RuntimeException("Patient with id " + patientId + " not found."));

    patient.setInsurance(insurance);
    insurance.setPatient(patient);

    return patientRepository.save(patient);
  }
}