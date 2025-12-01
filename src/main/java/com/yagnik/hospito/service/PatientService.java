package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
public class PatientService {

  private PatientRepository patientRepository;
    @Transactional
  public Patient getPatientById(Long Id) {

    Patient p1 = patientRepository.findById(Id)
        .orElseThrow(() -> new RuntimeException("Patient with id " + Id + " not found."));

    // System.out.println(p1);
    Patient p2 = patientRepository.findById(Id)
        .orElseThrow(() -> new RuntimeException("Patient with id " + Id + " not found."));
    // System.out.println(p2);
    return p1;
  }
	public Patient getPatientByName(String string) {
		return null;
	}

}