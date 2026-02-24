package com.yagnik.hospito.patient.entity;

import com.yagnik.hospito.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "insurance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Insurance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String policyNumber;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(nullable = false)
    private LocalDate validTill;

    @OneToOne(mappedBy = "insurance")
    private Patient patient;
}