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
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TeacherService tests: 2 unit tests (mocked dependencies) + 1 integration test (full app, H2 database).
 */
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
        request.setUsername("grace");
        request.setPassword("grace123");
        request.setFullName("Grace Hopper");
        request.setEmail("grace@university.edu");
        request.setDepartment("CSE");
        request.setDesignation("Lecturer");
        request.setQualification("MSc");
    }

    @Test
    void createTeacher_throwsDuplicate_whenUsernameTaken() {
        when(userRepository.existsByUsername("grace")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> teacherService.createTeacher(request));
        verify(teacherRepository, never()).save(any());
    }

    @Test
    void updateTeacher_updatesFields_andEncodesNewPassword() {
        Teacher existing = new Teacher("grace", "old-hash", "Grace Hopper", "grace@university.edu",
                "CSE", "Lecturer", "MSc");
        existing.setId(3L);
        when(teacherRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("grace123")).thenReturn("new-hash");
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));
        request.setDesignation("Professor");

        TeacherResponseDTO result = teacherService.updateTeacher(3L, request);

        assertEquals("Professor", result.getDesignation());
        verify(teacherRepository).save(argThat(t -> "new-hash".equals(t.getPassword())));
    }

    // ---------- Integration test ----------

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class Integration {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void teacher_canListTeachers_includingSeededTeacher() throws Exception {
            mockMvc.perform(get("/api/teachers").with(httpBasic("teacher", "teacher123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.username == 'teacher')].designation").value("Professor"));
        }
    }
}
