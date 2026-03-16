package com.biursite.controller;

import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.entity.User;
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
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private User alice;
    private User admin;
    private String aliceToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity aliceEntity = userRepository.save(UserEntity.builder()
                .username("alice").email("alice@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.ROLE_USER).createdAt(Instant.now()).build());
        UserEntity adminEntity = userRepository.save(UserEntity.builder()
                .username("admin").email("admin@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.ROLE_ADMIN).createdAt(Instant.now()).build());

        alice = com.biursite.domain.user.entity.User.builder()
                .id(aliceEntity.getId()).username(aliceEntity.getUsername()).email(aliceEntity.getEmail())
                .password(aliceEntity.getPassword()).role(aliceEntity.getRole()).createdAt(aliceEntity.getCreatedAt()).build();
        admin = com.biursite.domain.user.entity.User.builder()
                .id(adminEntity.getId()).username(adminEntity.getUsername()).email(adminEntity.getEmail())
                .password(adminEntity.getPassword()).role(adminEntity.getRole()).createdAt(adminEntity.getCreatedAt()).build();

        aliceToken = jwtUtil.generateToken("alice", "ROLE_USER");
        adminToken = jwtUtil.generateToken("admin", "ROLE_ADMIN");
    }

    @Test
    void listAllUsers_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void listAllUsers_asRegularUser_returns403() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_ownProfile_returns200() throws Exception {
        mockMvc.perform(get("/api/users/" + alice.getId())
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getUserById_otherProfile_asUser_returns403() throws Exception {
        mockMvc.perform(get("/api/users/" + admin.getId())
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_anyProfile_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/users/" + alice.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void createUser_asAdmin_returns201() throws Exception {
        String body = """
                {"username":"charlie","email":"charlie@test.com","password":"pass123"}
                """;
        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("charlie"));
    }

    @Test
    void createUser_asRegularUser_returns403() throws Exception {
        String body = """
                {"username":"charlie","email":"charlie@test.com","password":"pass123"}
                """;
        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_ownProfile_returns200() throws Exception {
        String body = """
                {"username":"alice_updated","email":"alice_new@test.com"}
                """;
        mockMvc.perform(put("/api/users/" + alice.getId())
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice_updated"));
    }

    @Test
    void updateUser_otherProfile_asUser_returns403() throws Exception {
        String body = """
                {"username":"hacked","email":"hacked@test.com"}
                """;
        mockMvc.perform(put("/api/users/" + admin.getId())
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_anyProfile_asAdmin_returns200() throws Exception {
        String body = """
                {"username":"alice_admin_edit","email":"alice_edit@test.com"}
                """;
        mockMvc.perform(put("/api/users/" + alice.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice_admin_edit"));
    }

    @Test
    void deleteUser_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/users/" + alice.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_asRegularUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/users/" + alice.getId())
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticated_request_returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}
