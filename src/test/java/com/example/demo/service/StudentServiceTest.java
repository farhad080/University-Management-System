package com.example.demo.service;

import com.example.demo.dto.StudentRequestDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * StudentService tests: 2 unit tests (mocked dependencies) + 1 integration test (full app, H2 database).
 */
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

    // ---------- Unit tests ----------

    @Test
    void createStudent_encodesPasswordAndSaves() {
        StudentRequestDTO request = new StudentRequestDTO();
        request.setUsername("alice");
        request.setPassword("secret123");
        request.setFullName("Alice Smith");
        request.setEmail("alice@university.edu");
        request.setDepartment("CSE");
        request.setStudentIdNumber("CSE-2024-001");
        request.setSemester(2);
        request.setCgpa(3.5);

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
        verify(studentRepository).save(argThat(s -> "hashed-secret".equals(s.getPassword())));
    }

    @Test
    void getStudentById_throwsNotFound_whenMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(99L));
    }

    // ---------- Integration test ----------

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class Integration {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void admin_canCreateStudent_andThenFetchIt() throws Exception {
            String body = """
                    {
                      "username": "integration_student",
                      "password": "pass1234",
                      "fullName": "Integration Student",
                      "email": "integration.student@university.edu",
                      "department": "EEE",
                      "studentIdNumber": "EEE-2024-900",
                      "semester": 3,
                      "cgpa": 3.2
                    }
                    """;

            String response = mockMvc.perform(post("/api/students")
                            .with(httpBasic("admin", "admin123"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.role").value("ROLE_STUDENT"))
                    .andReturn().getResponse().getContentAsString();

            String id = response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1");

            mockMvc.perform(get("/api/students/" + id).with(httpBasic("admin", "admin123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentIdNumber").value("EEE-2024-900"));
        }
    }
}
