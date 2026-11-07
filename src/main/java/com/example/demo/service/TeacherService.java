package com.example.demo.service;

import com.example.demo.dto.TeacherRequestDTO;
import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Teacher;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository teacherRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<TeacherResponseDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(TeacherResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherResponseDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        return TeacherResponseDTO.fromEntity(teacher);
    }

    public TeacherResponseDTO createTeacher(TeacherRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email '" + dto.getEmail() + "' is already registered");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for creating a new Teacher");
        }

        Teacher teacher = new Teacher();
        teacher.setUsername(dto.getUsername());
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setFullName(dto.getFullName());
        teacher.setEmail(dto.getEmail());
        teacher.setDepartment(dto.getDepartment());
        teacher.setDesignation(dto.getDesignation());
        teacher.setQualification(dto.getQualification());

        Teacher saved = teacherRepository.save(teacher);
        return TeacherResponseDTO.fromEntity(saved);
    }

    public TeacherResponseDTO updateTeacher(Long id, TeacherRequestDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        if (!teacher.getUsername().equalsIgnoreCase(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (!teacher.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email '" + dto.getEmail() + "' is already registered");
        }

        teacher.setUsername(dto.getUsername());
        teacher.setFullName(dto.getFullName());
        teacher.setEmail(dto.getEmail());
        teacher.setDepartment(dto.getDepartment());
        teacher.setDesignation(dto.getDesignation());
        teacher.setQualification(dto.getQualification());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Teacher updated = teacherRepository.save(teacher);
        return TeacherResponseDTO.fromEntity(updated);
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        teacherRepository.delete(teacher);
    }
}
