package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final PatientRepository patientRepository;

  @Transactional
  public Patient getPatientById(Long id) {
    return patientRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Patient with id " + id + " not found."));
  }

  public Patient getPatientByName(String name) {
    return patientRepository.getPatientByName(name);
  }
}