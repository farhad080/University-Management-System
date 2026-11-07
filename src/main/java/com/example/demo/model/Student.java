package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends User {

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "student_id_number", nullable = false, unique = true, length = 50)
    private String studentIdNumber;

    @Column
    private Integer semester;

    @Column
    private Double cgpa;

    public Student() {
        super();
        setRole(Role.ROLE_STUDENT);
    }

    public Student(String username, String password, String fullName, String email, String department, String studentIdNumber, Integer semester, Double cgpa) {
        super(username, password, fullName, email, Role.ROLE_STUDENT);
        this.department = department;
        this.studentIdNumber = studentIdNumber;
        this.semester = semester;
        this.cgpa = cgpa;
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
}
