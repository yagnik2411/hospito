package com.yagnik.hospito.dto;

import com.yagnik.hospito.entity.types.BloodGroupType;

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