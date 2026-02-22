package com.yagnik.hospito.service;

import com.yagnik.hospito.entity.Appointment;
import com.yagnik.hospito.entity.Doctor;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.AppointmentRepository;
import com.yagnik.hospito.repository.DoctorRepository;
import com.yagnik.hospito.repository.PatientRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final DoctorRepository doctorRepository;
  private final PatientRepository patientRepository;

  @Transactional
  public Appointment createNewAppointment(Appointment appointment, Long doctorId, Long patientId) {
    Patient patient = patientRepository.findById(patientId)
        .orElseThrow(() -> new RuntimeException("Patient with id " + patientId + " not found."));
    Doctor doctor = doctorRepository.findById(doctorId)
        .orElseThrow(() -> new RuntimeException("Doctor with id " + doctorId + " not found."));

    if (appointment.getId() != null) {
      throw new IllegalArgumentException("A new Appointment should not have an Id.");
    }

    appointment.setPatient(patient);
    appointment.setDoctor(doctor);

    return appointmentRepository.save(appointment);
  }
}