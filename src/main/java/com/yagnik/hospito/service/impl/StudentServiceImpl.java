package com.yagnik.hospito.service.impl;

import com.yagnik.hospito.service.StudentService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.yagnik.hospito.dto.AddStudentDto;
import com.yagnik.hospito.dto.StudentDto;
import com.yagnik.hospito.entity.Student;
import com.yagnik.hospito.repository.StudentRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// @Service
// @RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private StudentRepository studentRepository;
  private ModelMapper modelMapper;

  @Override
  public List<StudentDto> getAllStudents() {
    List<Student> students = studentRepository.findAll();

    List<StudentDto> studentDtos = students.stream()
        .map(student -> new StudentDto(student.getId(), student.getName(), student.getEmail())).toList();

    return studentDtos;
  }

  @Override
  public StudentDto getStudentById(Long id) {
    Optional<Student> studentOptional = studentRepository.findById(id);
    if (studentOptional.isPresent()) {
      Student student = studentOptional.get();
      return modelMapper.map(student, StudentDto.class);
    } else {
      // Handle the case where the student is not found, e.g., throw an exception
      throw new RuntimeException("Student with id " + id + " not found.");
    }
  }

  @Override
  public StudentDto createStudent(AddStudentDto addStudentDto) {
    Student student = modelMapper.map(addStudentDto, Student.class);
    student = studentRepository.save(student);
    return modelMapper.map(student, StudentDto.class);
  }

  @Override
  public void deleteStudentByID(Long id) {
    if (!studentRepository.existsById(id)) {
      throw new RuntimeException("Student with id " + id + " not found.");
    }
    studentRepository.deleteById(id);
  }

  @Override
  public StudentDto updateStudent(Long id, AddStudentDto addStudentDto) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student with id " + id + " not found."));
    modelMapper.map(addStudentDto, student);
    student = studentRepository.save(student);
    return modelMapper.map(student, StudentDto.class);
  }

  @Override
  public StudentDto updateStudentPatch(Long id, Map<String, Object> updates) {

    Student studentToUpdate = studentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Student with id " + id + " not found."));

    updates.forEach((key, value) -> {
      switch (key) {
        case "name":
          studentToUpdate.setName((String) value);
          break;
        case "email":
          studentToUpdate.setEmail((String) value);
          break;
        default:
          throw new IllegalArgumentException("Invalid field for update: " + key);
      }
    });

    Student savedStudent = studentRepository.save(studentToUpdate);

    return modelMapper.map(savedStudent, StudentDto.class);
  }
}