package com.example.demo.dto;

import com.example.demo.model.Admin;
import com.example.demo.model.Role;

import java.time.LocalDateTime;

public class AdminResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private String department;
    private String officeLocation;
    private LocalDateTime createdAt;

    public AdminResponseDTO() {
    }

    public static AdminResponseDTO fromEntity(Admin admin) {
        AdminResponseDTO dto = new AdminResponseDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setFullName(admin.getFullName());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole());
        dto.setDepartment(admin.getDepartment());
        dto.setOfficeLocation(admin.getOfficeLocation());
        dto.setCreatedAt(admin.getCreatedAt());
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

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
