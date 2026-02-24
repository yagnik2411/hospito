package com.yagnik.hospito.billing.repository;

import com.yagnik.hospito.billing.entity.Bill;
import com.yagnik.hospito.billing.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    List<Bill> findAllByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<Bill> findAllByBranchIdOrderByCreatedAtDesc(Long branchId);

    List<Bill> findAllByBranchIdAndStatusOrderByCreatedAtDesc(
        Long branchId, BillStatus status);
}