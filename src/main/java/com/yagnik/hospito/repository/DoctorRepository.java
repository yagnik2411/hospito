package com.yagnik.hospito.repository;

import java.time.LocalDate;
import java.util.List;

import com.yagnik.hospito.dto.BloodGroupCountResponseEntity;
import com.yagnik.hospito.entity.Appointment;
import com.yagnik.hospito.entity.Department;
import com.yagnik.hospito.entity.Doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}