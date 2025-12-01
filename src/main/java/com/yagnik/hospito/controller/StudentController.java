package com.yagnik.hospito.controller;

import java.util.List;
import java.util.Map;

import com.yagnik.hospito.dto.AddStudentDto;
import com.yagnik.hospito.dto.StudentDto;
import com.yagnik.hospito.service.StudentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

// @RestController
// @RequiredArgsConstructor
public class StudentController {

  private StudentService studentService;

  // StudentController(StudentService studentService){
  // this.studentService = studentService;
  // }

  @GetMapping("/students")
  public ResponseEntity<List<StudentDto>> getStudent() {
    return ResponseEntity.status(HttpStatus.OK).body(studentService.getAllStudents());
  }

  @GetMapping("/students/{id}")
  public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(studentService.getStudentById(id));
  }

  @PostMapping("/students")
  public ResponseEntity<StudentDto> createStudent(@RequestBody AddStudentDto addStudentDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(addStudentDto));

  }

  @DeleteMapping("/students/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    studentService.deleteStudentByID(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PutMapping("/students/{id}")
  public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @RequestBody AddStudentDto addStudentDto) {
    return ResponseEntity.status(HttpStatus.OK).body(studentService.updateStudent(id, addStudentDto));
  }

  @PatchMapping("/students/{id}")
  public ResponseEntity<StudentDto> updateStudentPatch(@PathVariable Long id, @RequestBody Map<String, Object> updates){
    return ResponseEntity.status(HttpStatus.OK).body(studentService.updateStudentPatch(id, updates));
  }
}
