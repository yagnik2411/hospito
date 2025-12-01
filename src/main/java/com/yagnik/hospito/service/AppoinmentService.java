package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Appoinment;
import com.yagnik.hospito.entity.Doctor;
import com.yagnik.hospito.entity.Insurance;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.AppoinmentRepository;
import com.yagnik.hospito.repository.DoctorRepository;
import com.yagnik.hospito.repository.InsuranceRepository;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// @Service
//   @RequiredArgsConstructor
  public class AppoinmentService{

    private AppoinmentRepository appoinmentRepository;
    private DoctorRepository doctorRespository;
    private PatientRepository patientRepository;


    @Transactional
    public Appoinment createNewAppoinment(Appoinment appoinment,Long doctorId,Long patientId){
      Patient patient = patientRepository.findById(patientId).orElseThrow();
      Doctor doctor = doctorRespository.findById(doctorId).orElseThrow();

      if(appoinment.getId() != null) throw new IllegalArgumentException("A new Appoinment should not have an Id.");


      appoinment.setPatient(patient);
      appoinment.setDoctor(doctor);

      patient.getAppoinments().add(appoinment);
        
      return appoinmentRepository.save(appoinment);
      
     
    }
  }