// package com.yagnik.hospito;

// import java.time.LocalDate;


// import com.yagnik.hospito.entity.Insurance;
// import com.yagnik.hospito.entity.Patient;
// import com.yagnik.hospito.repository.PatientRepository;
// import com.yagnik.hospito.service.InsuranceService;
// import com.yagnik.hospito.service.PatientService;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;

// import ch.qos.logback.core.joran.action.PreconditionValidator;
// import jakarta.transaction.Transactional;

// @SpringBootTest
// class InsuranceTests {

//   @Autowired
//   private InsuranceService insuranceService;

//   @Test
//   public void testInsurance() {
//     Insurance insurance = Insurance.builder().policyNumber("HDFC_1234").provider("HDFC")
//         .validTill(LocalDate.of(2030, 12, 12))
//         .build();

//     Patient patient = insuranceService.assignInsuranceToPatient(insurance, 1L);
//     // System.out.println(patient);
//   }

// }