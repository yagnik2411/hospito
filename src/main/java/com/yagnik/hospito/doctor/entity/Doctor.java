package com.yagnik.hospito.doctor.entity;

import com.yagnik.hospito.auth.entity.User;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 50)
  private String specialization;

  @Column(nullable = false, unique = true, length = 50)
  private String licenseNumber;

  @Column(length = 500)
  private String bio;

  @Column(nullable = false)
  @Builder.Default
  private boolean isActive = true;

  // Auth linkage — OneToOne with User
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // Primary branch — where this doctor was originally created
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "primary_branch_id", nullable = false)
  private Branch primaryBranch;

  // All branches this doctor works at (ManyToMany)
  @ManyToMany
  @JoinTable(name = "branch_doctors", joinColumns = @JoinColumn(name = "doctor_id"), inverseJoinColumns = @JoinColumn(name = "branch_id"))
  @Builder.Default
  private Set<Branch> branches = new HashSet<>();

  // Availability slots
  @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<DoctorAvailability> availabilitySlots = new HashSet<>();
}