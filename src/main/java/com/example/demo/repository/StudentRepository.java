package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByStudentIdNumber(String studentIdNumber);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByStudentIdNumber(String studentIdNumber);
}
