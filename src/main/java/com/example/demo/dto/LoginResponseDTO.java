package com.example.demo.dto;

import com.example.demo.model.Role;

public class LoginResponseDTO {
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private String message;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String username, String fullName, String email, Role role, String message) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
