package com.example.demo.service;

import com.example.demo.dto.StudentRequestDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return StudentResponseDTO.fromEntity(student);
    }

    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email '" + dto.getEmail() + "' is already registered");
        }
        if (studentRepository.existsByStudentIdNumber(dto.getStudentIdNumber())) {
            throw new DuplicateResourceException("Student ID Number '" + dto.getStudentIdNumber() + "' is already registered");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for creating a new Student");
        }

        Student student = new Student();
        student.setUsername(dto.getUsername());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setDepartment(dto.getDepartment());
        student.setStudentIdNumber(dto.getStudentIdNumber());
        student.setSemester(dto.getSemester());
        student.setCgpa(dto.getCgpa());

        Student saved = studentRepository.save(student);
        return StudentResponseDTO.fromEntity(saved);
    }

    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        if (!student.getUsername().equalsIgnoreCase(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (!student.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email '" + dto.getEmail() + "' is already registered");
        }
        if (!student.getStudentIdNumber().equalsIgnoreCase(dto.getStudentIdNumber()) && studentRepository.existsByStudentIdNumber(dto.getStudentIdNumber())) {
            throw new DuplicateResourceException("Student ID Number '" + dto.getStudentIdNumber() + "' is already registered");
        }

        student.setUsername(dto.getUsername());
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setDepartment(dto.getDepartment());
        student.setStudentIdNumber(dto.getStudentIdNumber());
        student.setSemester(dto.getSemester());
        student.setCgpa(dto.getCgpa());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Student updated = studentRepository.save(student);
        return StudentResponseDTO.fromEntity(updated);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        studentRepository.delete(student);
    }
}
