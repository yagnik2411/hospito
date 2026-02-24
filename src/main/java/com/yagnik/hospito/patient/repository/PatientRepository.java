package com.yagnik.hospito.patient.repository;

import com.yagnik.hospito.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // All patients who visited a specific branch
    @Query("SELECT p FROM Patient p JOIN p.visitedBranches b WHERE b.id = :branchId AND p.isActive = true")
    List<Patient> findAllByBranchId(@Param("branchId") Long branchId);

    // Search by name within a branch
    @Query("SELECT p FROM Patient p JOIN p.visitedBranches b WHERE b.id = :branchId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
    List<Patient> findByBranchIdAndNameContaining(
        @Param("branchId") Long branchId,
        @Param("name") String name);
}