package com.example.demo.service;

import com.example.demo.dto.StudentRequestDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new StudentRequestDTO();
        request.setUsername("alice");
        request.setPassword("secret123");
        request.setFullName("Alice Smith");
        request.setEmail("alice@university.edu");
        request.setDepartment("CSE");
        request.setStudentIdNumber("CSE-2024-001");
        request.setSemester(2);
        request.setCgpa(3.5);
    }

    @Test
    void createStudent_encodesPasswordAndSaves() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@university.edu")).thenReturn(false);
        when(studentRepository.existsByStudentIdNumber("CSE-2024-001")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-secret");
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        StudentResponseDTO result = studentService.createStudent(request);

        assertEquals(10L, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals(3.5, result.getCgpa());
        verify(studentRepository).save(argThat(s -> "hashed-secret".equals(s.getPassword())));
    }

    @Test
    void createStudent_duplicateUsername_throwsAndDoesNotSave() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void getStudentById_notFound_throwsWithMessage() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> studentService.getStudentById(99L));
        assertEquals("Student not found with ID: 99", ex.getMessage());
    }
}
