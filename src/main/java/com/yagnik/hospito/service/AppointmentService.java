package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Appointment;
import com.yagnik.hospito.entity.Doctor;
import com.yagnik.hospito.entity.Insurance;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.AppointmentRepository;
import com.yagnik.hospito.repository.DoctorRepository;
import com.yagnik.hospito.repository.InsuranceRepository;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// @Service
//   @RequiredArgsConstructor
public class AppointmentService {

  private AppointmentRepository AppointmentRepository;
  private DoctorRepository doctorRespository;
  private PatientRepository patientRepository;

  @Transactional
  public Appointment createNewAppointment(Appointment Appointment, Long doctorId, Long patientId) {
    Patient patient = patientRepository.findById(patientId).orElseThrow();
    Doctor doctor = doctorRespository.findById(doctorId).orElseThrow();

    if (Appointment.getId() != null)
      throw new IllegalArgumentException("A new Appointment should not have an Id.");

    Appointment.setPatient(patient);
    Appointment.setDoctor(doctor);

    patient.getAppointments().add(Appointment);

    return AppointmentRepository.save(Appointment);

  }
}