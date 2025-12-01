package com.yagnik.hospito.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
public class Doctor{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  @Column(nullable = false,length = 100)
  private String name;

  @Column(length = 30)
  private String specilazation;

  @Column(length = 10,nullable = false,unique = true)
  private String email;

  @CreationTimestamp
  @Column(nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "doctor")
  private List<Appoinment> appoinments;

  @OneToOne(mappedBy = "headDoctor")
  private Department department;

  @ManyToMany(mappedBy = "doctors")
  private Set<Department> deparments = new HashSet<>();
}