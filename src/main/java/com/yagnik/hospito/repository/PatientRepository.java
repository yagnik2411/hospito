package com.yagnik.hospito.repository;

import java.time.LocalDate;
import java.util.List;

import com.yagnik.hospito.dto.BloodGroupCountResponseEntity;
import com.yagnik.hospito.entity.Patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	Patient getPatientByName(String string);

	List<Patient> getPatientByNameOrDateOfBirth(String name, LocalDate dateOfBirth);

	@Query("SELECT p FROM Patient p WHERE p.gender = ?1")
	List<Patient> getPatientByGender(String gender);

	@Query("SELECT p FROM Patient p WHERE p.dateOfBirth > :dateOfBirth")
	List<Patient> getPatientAfterDateOfBirth(@Param("dateOfBirth") LocalDate dateOfBirth);

	// @Query(value = "select * from patient", nativeQuery = true)
	@Query("SELECT p FROM Patient p")
	Page<Patient> findAllPatient(Pageable pageable);

	// @Query("select p.bloodGroup,count(p) from Patient p group by p.bloodGroup")
	@Query("select new com.yagnik.hospito.dto.BloodGroupCountResponseEntity(p.bloodGroup,count(p)) from Patient p group by p.bloodGroup")
	// List<Object> countEachBloodGroupType();
	List<BloodGroupCountResponseEntity> countEachBloodGroupType();

	@Transactional
	@Modifying
	@Query(value = "update patient p set p.name = :name where p.id = :id", nativeQuery = true) // <-- Add this
	Integer updateNameById(@Param("name") String name, @Param("id") Long id);

}