package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Insurance;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.InsuranceRepository;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// @Service
//   @RequiredArgsConstructor
public class InsuranceService {

  private InsuranceRepository insuranceRepository;
  private PatientRepository patientRepository;

  @Transactional
  public Patient assignInsuranceToPatient(Insurance insurance,Long patientId){
    Patient patient= patientRepository.findById(patientId).orElseThrow();

    patient.setInsurance(insurance);
    insurance.setPatient(patient);

    return patient;
  }

}
