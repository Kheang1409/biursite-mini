package com.biursite;

import com.biursite.infrastructure.persistence.PostRepository;
import com.biursite.infrastructure.persistence.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end test validating the full lifecycle:
 * Register -> Login -> Create Post -> Update Post -> Delete Post -> Admin operations
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EndToEndFlowTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void fullUserAndPostLifecycle() throws Exception {
        // 1. Register user "alice"
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","email":"alice@test.com","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String aliceToken = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .get("token").asText();

        // 2. Login with same credentials
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String loginToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        // 3. Create a post using login token
        MvcResult createResult = mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + loginToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"My First Post","content":"Hello from Alice!"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("My First Post"))
                .andExpect(jsonPath("$.authorUsername").value("alice"))
                .andReturn();

        JsonNode postNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long postId = postNode.get("id").asLong();

        // 4. Get post by ID
        mockMvc.perform(get("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My First Post"));

        // 5. Update own post
        mockMvc.perform(put("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + loginToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated Post","content":"Updated content"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post"))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        // 6. Register "bob" and attempt to update alice's post
        MvcResult bobRegister = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"bob","email":"bob@test.com","password":"pass456"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String bobToken = objectMapper.readTree(bobRegister.getResponse().getContentAsString())
                .get("token").asText();

        // 7. Bob attempts to update Alice's post -> 403
        mockMvc.perform(put("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + bobToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Hacked","content":"Hacked"}
                                """))
                .andExpect(status().isForbidden());

        // 8. Bob attempts to delete Alice's post -> 403
        mockMvc.perform(delete("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isForbidden());

        // 9. Alice deletes her own post -> 204
        mockMvc.perform(delete("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isNoContent());

        // 10. Verify post is gone -> 404
        mockMvc.perform(get("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isNotFound());

        // 11. Listing posts should be empty
        mockMvc.perform(get("/api/posts")
                        .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void adminCanDeleteAnyPost() throws Exception {
        // Register regular user
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user1","email":"user1@test.com","password":"pass"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String userToken = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        // User creates a post
        MvcResult postResult = mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"User Post","content":"Content"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long postId = objectMapper.readTree(postResult.getResponse().getContentAsString()).get("id").asLong();

        // Create admin user directly in DB (can't register as admin via API)
        var adminUser = com.biursite.infrastructure.persistence.UserEntity.builder()
                .username("superadmin").email("superadmin@test.com")
                .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin"))
                .role(com.biursite.domain.user.entity.Role.ROLE_ADMIN)
                .createdAt(java.time.Instant.now())
                .build();
        userRepository.save(adminUser);

        // Login as admin
        MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"superadmin","password":"admin"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString()).get("token").asText();

        // Admin deletes user's post
        mockMvc.perform(delete("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateRegistration_returns400() throws Exception {
        // Register once
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"dup","email":"dup@test.com","password":"pass"}
                                """))
                .andExpect(status().isOk());

        // Same username
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"dup","email":"other@test.com","password":"pass"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is already taken"));

        // Same email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"other","email":"dup@test.com","password":"pass"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }
}
