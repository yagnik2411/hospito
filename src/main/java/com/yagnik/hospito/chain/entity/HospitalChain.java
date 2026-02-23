package com.yagnik.hospito.chain.entity;

import com.yagnik.hospito.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospital_chain")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HospitalChain extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(nullable = false)
    private Integer foundedYear;

    @Column(nullable = false, length = 100)
    private String headOfficeAddress;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;
}