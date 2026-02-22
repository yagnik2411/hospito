package com.yagnik.hospito.entity;

import java.time.LocalDate;
import java.util.List;

import com.yagnik.hospito.entity.types.BloodGroupType;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@Table(name = "patient", uniqueConstraints = {
                @UniqueConstraint(name = "unique_name_birthdate", columnNames = { "name", "dateOfBirth" })
}, indexes = {
                @Index(name = "idx_patient_birth_date", columnList = "dateOfBirth"),
})

public class Patient {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 50)
        private String name;

        private String gender;

        @Enumerated(EnumType.STRING)
        private BloodGroupType bloodGroup;

        private int age;

        @ToString.Exclude
        private LocalDate dateOfBirth;

        @Column(length = 50)
        private String email;

        @CreationTimestamp
        private LocalDate createdAt;

        @OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
        @JoinColumn(name = "patient_insurace_id")
        private Insurance insurance;

        @OneToMany(mappedBy = "patient", cascade = { CascadeType.REMOVE }, orphanRemoval = true)

        private List<Appointment> Appointments;
}