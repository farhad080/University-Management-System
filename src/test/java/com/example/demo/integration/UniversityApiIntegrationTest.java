package com.example.demo.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full application context + H2 in-memory database, seeded by DataInitializer.
 */
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class UniversityApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void admin_canListSeededStudents() throws Exception {
        mockMvc.perform(get("/api/students").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("student"))
                .andExpect(jsonPath("$[0].studentIdNumber").value("CSE-2023-001"));
    }

    @Test
    void student_cannotAccessTeacherEndpoints() throws Exception {
        mockMvc.perform(get("/api/teachers").with(httpBasic("student", "student123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canCreateStudent() throws Exception {
        String body = """
                {
                  "username": "carol",
                  "password": "carol123",
                  "fullName": "Carol White",
                  "email": "carol@university.edu",
                  "department": "CSE",
                  "studentIdNumber": "CSE-2024-777",
                  "semester": 1,
                  "cgpa": 3.2
                }
                """;

        mockMvc.perform(post("/api/students")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("carol"));
    }
}
