// package com.yagnik.hospito;

// import com.yagnik.hospito.entity.Patient;
// import com.yagnik.hospito.repository.PatientRepository;
// import com.yagnik.hospito.service.PatientService;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;

// @SpringBootTest
// class PatientTests {

//   @Autowired
//   private PatientRepository patientRepository;
//   @Autowired
//   private PatientService patientService;

//   @Test
//   public void testPatientRepository() {
//     // List<Patient> patients = patientRepository.findAll();
//     // System.out.println(patients);
    
//   }

//   @Test
//   public void testPatientService() {
//     // LocalDate birthDate = LocalDate.of(1983, 3, 20);

//     // Integer patient = patientRepository.updateNameById("Yagnik", 1L);
//     // System.out.println(patient);
//     // List<BloodGroupCountResponseEntity> bloodGroupList = patientRepository.countEachBloodGroupType();
//     //        for(BloodGroupCountResponseEntity bloodGroupCountResponse: bloodGroupList) {
//     //            System.out.println(bloodGroupCountResponse);
//     //        }
//     // Page<Patient> patientList = patientRepository.findAllPatient(PageRequest.of(0,2));

//     // for(Patient patient: patientList) {
//     //     System.out.println(patient);
//     // }
//   }
// }