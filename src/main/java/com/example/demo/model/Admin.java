package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {

    @Column(length = 100)
    private String department;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    public Admin() {
        super();
        setRole(Role.ROLE_ADMIN);
    }

    public Admin(String username, String password, String fullName, String email, String department, String officeLocation) {
        super(username, password, fullName, email, Role.ROLE_ADMIN);
        this.department = department;
        this.officeLocation = officeLocation;
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
}
