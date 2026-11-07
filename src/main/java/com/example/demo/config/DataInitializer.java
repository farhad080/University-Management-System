package com.example.demo.config;

import com.example.demo.model.Admin;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AdminRepository adminRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Seeding initial users for University Management System...");

            // 1. Seed Admin
            Admin admin = new Admin(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    "admin@university.edu",
                    "Information Technology",
                    "Building A, Room 302"
            );
            adminRepository.save(admin);
            log.info("Default Admin seeded: username='admin', password='admin123'");

            // 2. Seed Teacher
            Teacher teacher = new Teacher(
                    "teacher",
                    passwordEncoder.encode("teacher123"),
                    "Dr. Alan Turing",
                    "alan.turing@university.edu",
                    "Computer Science & Engineering",
                    "Professor",
                    "PhD in Computer Science"
            );
            teacherRepository.save(teacher);
            log.info("Default Teacher seeded: username='teacher', password='teacher123'");

            // 3. Seed Student
            Student student = new Student(
                    "student",
                    passwordEncoder.encode("student123"),
                    "John Doe",
                    "john.doe@university.edu",
                    "Computer Science & Engineering",
                    "CSE-2023-001",
                    5,
                    3.85
            );
            studentRepository.save(student);
            log.info("Default Student seeded: username='student', password='student123'");

            log.info("Data initialization complete. All roles seeded successfully.");
        }
    }
}
