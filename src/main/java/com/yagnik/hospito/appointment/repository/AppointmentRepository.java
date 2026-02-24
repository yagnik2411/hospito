package com.yagnik.hospito.appointment.repository;

import com.yagnik.hospito.appointment.entity.Appointment;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Conflict detection — same doctor, same time, not cancelled/no-show
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentTime = :appointmentTime " +
           "AND a.status NOT IN (:excludedStatuses)")
    boolean existsConflict(
        @Param("doctorId") Long doctorId,
        @Param("appointmentTime") LocalDateTime appointmentTime,
        @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);

    // All appointments for a branch
    List<Appointment> findAllByBranchIdOrderByAppointmentTimeDesc(Long branchId);

    // All appointments for a branch with status filter
    List<Appointment> findAllByBranchIdAndStatusOrderByAppointmentTimeDesc(
        Long branchId, AppointmentStatus status);

    // All appointments for a doctor
    List<Appointment> findAllByDoctorIdOrderByAppointmentTimeDesc(Long doctorId);

    // All appointments for a patient
    List<Appointment> findAllByPatientIdOrderByAppointmentTimeDesc(Long patientId);

    // Doctor's appointments with status filter
    List<Appointment> findAllByDoctorIdAndStatusOrderByAppointmentTimeDesc(
        Long doctorId, AppointmentStatus status);
}