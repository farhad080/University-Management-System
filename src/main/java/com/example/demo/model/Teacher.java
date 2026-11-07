package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher extends User {

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(length = 150)
    private String qualification;

    public Teacher() {
        super();
        setRole(Role.ROLE_TEACHER);
    }

    public Teacher(String username, String password, String fullName, String email, String department, String designation, String qualification) {
        super(username, password, fullName, email, Role.ROLE_TEACHER);
        this.department = department;
        this.designation = designation;
        this.qualification = qualification;
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
}
