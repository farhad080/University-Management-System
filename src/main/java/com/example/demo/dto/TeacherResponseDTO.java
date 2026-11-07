package com.example.demo.dto;

import com.example.demo.model.Role;
import com.example.demo.model.Teacher;

import java.time.LocalDateTime;

public class TeacherResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private String department;
    private String designation;
    private String qualification;
    private LocalDateTime createdAt;

    public TeacherResponseDTO() {
    }

    public static TeacherResponseDTO fromEntity(Teacher teacher) {
        TeacherResponseDTO dto = new TeacherResponseDTO();
        dto.setId(teacher.getId());
        dto.setUsername(teacher.getUsername());
        dto.setFullName(teacher.getFullName());
        dto.setEmail(teacher.getEmail());
        dto.setRole(teacher.getRole());
        dto.setDepartment(teacher.getDepartment());
        dto.setDesignation(teacher.getDesignation());
        dto.setQualification(teacher.getQualification());
        dto.setCreatedAt(teacher.getCreatedAt());
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
