package com.example.demo.service;

import com.example.demo.dto.TeacherRequestDTO;
import com.example.demo.dto.TeacherResponseDTO;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.model.Teacher;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private TeacherRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new TeacherRequestDTO();
        request.setUsername("bob");
        request.setPassword("teach123");
        request.setFullName("Bob Brown");
        request.setEmail("bob@university.edu");
        request.setDepartment("EEE");
        request.setDesignation("Lecturer");
        request.setQualification("MSc");
    }

    @Test
    void getAllTeachers_mapsEntitiesToDtos() {
        Teacher t1 = new Teacher("t1", "pw", "Teacher One", "t1@university.edu", "CSE", "Professor", "PhD");
        Teacher t2 = new Teacher("t2", "pw", "Teacher Two", "t2@university.edu", "EEE", "Lecturer", "MSc");
        when(teacherRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TeacherResponseDTO> result = teacherService.getAllTeachers();

        assertEquals(2, result.size());
        assertEquals("Teacher One", result.get(0).getFullName());
        assertEquals("Lecturer", result.get(1).getDesignation());
    }

    @Test
    void createTeacher_duplicateEmail_throws() {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@university.edu")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> teacherService.createTeacher(request));
        verify(teacherRepository, never()).save(any());
    }

    @Test
    void updateTeacher_withoutPassword_keepsExistingPassword() {
        Teacher existing = new Teacher("bob", "old-hash", "Bob Brown", "bob@university.edu", "EEE", "Lecturer", "MSc");
        existing.setId(5L);
        request.setPassword(null);
        request.setDesignation("Assistant Professor");

        when(teacherRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

        TeacherResponseDTO result = teacherService.updateTeacher(5L, request);

        assertEquals("Assistant Professor", result.getDesignation());
        assertEquals("old-hash", existing.getPassword());
        verifyNoInteractions(passwordEncoder);
    }
}
