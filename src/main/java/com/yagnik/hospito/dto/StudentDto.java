package com.yagnik.hospito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
  public StudentDto(long l, String string, String string2) {
  }

  private Long id;
  private String name;
  private String email;
}