package com.yagnik.hospito.service;

import com.yagnik.hospito.dto.AddStudentDto;
import com.yagnik.hospito.dto.StudentDto;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface StudentService {
  public List<StudentDto> getAllStudents();

  public StudentDto getStudentById(Long id);

  public StudentDto createStudent(AddStudentDto addStudentDto);

  public void deleteStudentByID(Long id);

public StudentDto updateStudent(Long id, AddStudentDto addStudentDto);

public StudentDto updateStudentPatch(Long id, Map<String, Object> updates);

}
