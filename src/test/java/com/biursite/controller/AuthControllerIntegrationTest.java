package com.biursite.controller;

import com.biursite.domain.user.entity.Role;
import com.biursite.infrastructure.persistence.UserEntity;
import com.biursite.infrastructure.persistence.PostRepository;
import com.biursite.infrastructure.persistence.UserRepository;
import com.biursite.infrastructure.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_newUser_returns200WithToken() throws Exception {
        String body = """
                {"username":"alice","email":"alice@test.com","password":"pass123"}
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void register_duplicateUsername_returns400() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("alice").email("alice@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.ROLE_USER).createdAt(Instant.now()).build());

        String body = """
                {"username":"alice","email":"newemail@test.com","password":"pass123"}
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    void register_blankUsername_returns400WithValidationError() throws Exception {
        String body = """
                {"username":"","email":"valid@test.com","password":"pass123"}
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("alice").email("alice@test.com")
                .password(passwordEncoder.encode("secret"))
                .role(Role.ROLE_USER).createdAt(Instant.now()).build());

        String body = """
                {"username":"alice","password":"secret"}
                """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        userRepository.save(UserEntity.builder()
                .username("alice").email("alice@test.com")
                .password(passwordEncoder.encode("secret"))
                .role(Role.ROLE_USER).createdAt(Instant.now()).build());

        String body = """
                {"username":"alice","password":"wrong"}
                """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void login_nonExistentUser_returns401() throws Exception {
        String body = """
                {"username":"nobody","password":"pass"}
                """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
