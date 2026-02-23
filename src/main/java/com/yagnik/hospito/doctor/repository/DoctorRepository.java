package com.yagnik.hospito.doctor.repository;

import com.yagnik.hospito.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find all doctors in a specific branch
    @Query("SELECT d FROM Doctor d JOIN d.branches b WHERE b.id = :branchId")
    List<Doctor> findAllByBranchId(@Param("branchId") Long branchId);

    // Find all active doctors in a specific branch
    @Query("SELECT d FROM Doctor d JOIN d.branches b WHERE b.id = :branchId AND d.isActive = true")
    List<Doctor> findActiveByBranchId(@Param("branchId") Long branchId);

    // Search by specialization within a branch
    @Query("SELECT d FROM Doctor d JOIN d.branches b WHERE b.id = :branchId AND d.specialization = :specialization AND d.isActive = true")
    List<Doctor> findByBranchIdAndSpecialization(
            @Param("branchId") Long branchId,
            @Param("specialization") String specialization);

    // Find by license number
    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    // Find by user id
    Optional<Doctor> findByUserId(Long userId);

    // Check if license number exists
    boolean existsByLicenseNumber(String licenseNumber);
}