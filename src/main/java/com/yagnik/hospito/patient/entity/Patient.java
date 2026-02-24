package com.yagnik.hospito.patient.entity;

import com.yagnik.hospito.auth.entity.User;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.common.entity.AuditableEntity;
import com.yagnik.hospito.entity.types.BloodGroupType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;

    @Column(length = 15)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    // Auth linkage
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Insurance (optional)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "insurance_id")
    private Insurance insurance;

    // All branches this patient has visited
    @ManyToMany
    @JoinTable(
        name = "patient_branches",
        joinColumns = @JoinColumn(name = "patient_id"),
        inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    @Builder.Default
    private Set<Branch> visitedBranches = new HashSet<>();

    // Medical records
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicalRecord> medicalRecords = new ArrayList<>();
}