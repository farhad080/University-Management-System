package com.example.demo.service;

import com.example.demo.dto.AdminRequestDTO;
import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
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
 * AdminService tests: 2 unit tests (mocked dependencies) + 1 integration test (full app, H2 database).
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    // ---------- Unit tests ----------

    @Test
    void createAdmin_encodesPasswordAndSaves() {
        AdminRequestDTO request = new AdminRequestDTO();
        request.setUsername("root");
        request.setPassword("rootpass");
        request.setFullName("Root Admin");
        request.setEmail("root@university.edu");
        request.setDepartment("IT");
        request.setOfficeLocation("Building B");

        when(userRepository.existsByUsername("root")).thenReturn(false);
        when(userRepository.existsByEmail("root@university.edu")).thenReturn(false);
        when(passwordEncoder.encode("rootpass")).thenReturn("hashed-rootpass");
        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> {
            Admin a = inv.getArgument(0);
            a.setId(7L);
            return a;
        });

        AdminResponseDTO result = adminService.createAdmin(request);

        assertEquals(7L, result.getId());
        assertEquals("root", result.getUsername());
        verify(adminRepository).save(argThat(a -> "hashed-rootpass".equals(a.getPassword())));
    }

    @Test
    void deleteAdmin_throwsNotFound_whenMissing() {
        when(adminRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteAdmin(42L));
        verify(adminRepository, never()).delete(any());
    }

    // ---------- Integration test ----------

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    class Integration {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void admin_canCreateAnotherAdmin_butStudentIsForbidden() throws Exception {
            String body = """
                    {
                      "username": "second_admin",
                      "password": "admin456",
                      "fullName": "Second Admin",
                      "email": "second.admin@university.edu",
                      "department": "Registry",
                      "officeLocation": "Building C"
                    }
                    """;

            mockMvc.perform(post("/api/admins")
                            .with(httpBasic("admin", "admin123"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("second_admin"))
                    .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));

            mockMvc.perform(get("/api/admins").with(httpBasic("student", "student123")))
                    .andExpect(status().isForbidden());
        }
    }
}
