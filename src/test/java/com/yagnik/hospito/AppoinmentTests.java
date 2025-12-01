package com.yagnik.hospito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.yagnik.hospito.entity.Appoinment;
import com.yagnik.hospito.entity.Insurance;
import com.yagnik.hospito.entity.Patient;
import com.yagnik.hospito.repository.PatientRepository;
import com.yagnik.hospito.service.AppoinmentService;
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
  private AppoinmentService appoinmentService;

  @Test
  @Transactional
  public void testAppoinment() {
    Appoinment appoinment = Appoinment.builder().appoinmentTime(LocalDateTime.of(2025, 12, 1, 12, 00, 00))
        .reason("cancer").build();

    var newAppoinment = appoinmentService.createNewAppoinment(appoinment, 1L, 2L);
    System.out.println("testAppoinment: "+newAppoinment);
  }

}