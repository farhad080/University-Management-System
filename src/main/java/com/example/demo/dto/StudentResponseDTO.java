package com.example.demo.dto;

import com.example.demo.model.Role;
import com.example.demo.model.Student;

import java.time.LocalDateTime;

public class StudentResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private String department;
    private String studentIdNumber;
    private Integer semester;
    private Double cgpa;
    private LocalDateTime createdAt;

    public StudentResponseDTO() {
    }

    public static StudentResponseDTO fromEntity(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setRole(student.getRole());
        dto.setDepartment(student.getDepartment());
        dto.setStudentIdNumber(student.getStudentIdNumber());
        dto.setSemester(student.getSemester());
        dto.setCgpa(student.getCgpa());
        dto.setCreatedAt(student.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStudentIdNumber() {
        return studentIdNumber;
    }

    public void setStudentIdNumber(String studentIdNumber) {
        this.studentIdNumber = studentIdNumber;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
