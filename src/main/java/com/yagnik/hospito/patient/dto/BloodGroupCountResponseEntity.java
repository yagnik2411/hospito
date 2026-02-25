package com.yagnik.hospito.patient.dto;

import com.yagnik.hospito.patient.enums.BloodGroupType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodGroupCountResponseEntity {
  private BloodGroupType bloodGroupType;
  private Long count;
}