package com.yagnik.hospito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.yagnik.hospito.entity.Appointment;
import com.yagnik.hospito.entity.Insurance;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.PatientRepository;
import com.yagnik.hospito.service.AppointmentService;
import com.yagnik.hospito.service.InsuranceService;
import com.yagnik.hospito.service.PatientService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import jakarta.transaction.Transactional;

@SpringBootTest
class AppoinmnetTests {

    @Autowired
    private AppointmentService AppointmentService;

    @Test
    @Transactional
    public void testAppointment() {
        Appointment appointment = Appointment.builder().AppointmentTime(LocalDateTime.of(2025, 12, 1, 12, 00, 00))
                .reason("cancer").build();

        var newAppointment = AppointmentService.createNewAppointment(appointment, 1L, 2L);
        System.out.println("testAppointment: " + newAppointment);
    }

}
