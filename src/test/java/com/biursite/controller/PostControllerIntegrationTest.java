package com.biursite.controller;

import com.biursite.infrastructure.persistence.PostEntity;
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
class PostControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

        private User alice;
        private User bob;
        private User admin;
        private UserEntity aliceEntity;
        private UserEntity bobEntity;
        private UserEntity adminEntity;
    private String aliceToken;
    private String bobToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        aliceEntity = userRepository.save(UserEntity.builder()
                .username("alice").email("alice@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.ROLE_USER).createdAt(Instant.now()).build());
        bobEntity = userRepository.save(UserEntity.builder()
                .username("bob").email("bob@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.ROLE_USER).createdAt(Instant.now()).build());
        adminEntity = userRepository.save(UserEntity.builder()
                .username("admin").email("admin@test.com")
                .password(passwordEncoder.encode("pass"))
                .role(Role.ROLE_ADMIN).createdAt(Instant.now()).build());

        alice = com.biursite.domain.user.entity.User.builder()
                .id(aliceEntity.getId()).username(aliceEntity.getUsername()).email(aliceEntity.getEmail())
                .password(aliceEntity.getPassword()).role(aliceEntity.getRole()).createdAt(aliceEntity.getCreatedAt()).build();
        bob = com.biursite.domain.user.entity.User.builder()
                .id(bobEntity.getId()).username(bobEntity.getUsername()).email(bobEntity.getEmail())
                .password(bobEntity.getPassword()).role(bobEntity.getRole()).createdAt(bobEntity.getCreatedAt()).build();
        admin = com.biursite.domain.user.entity.User.builder()
                .id(adminEntity.getId()).username(adminEntity.getUsername()).email(adminEntity.getEmail())
                .password(adminEntity.getPassword()).role(adminEntity.getRole()).createdAt(adminEntity.getCreatedAt()).build();

        aliceToken = jwtUtil.generateToken("alice", "ROLE_USER");
        bobToken = jwtUtil.generateToken("bob", "ROLE_USER");
        adminToken = jwtUtil.generateToken("admin", "ROLE_ADMIN");
    }

    @Test
    void createPost_authenticated_returns201() throws Exception {
        String body = """
                {"title":"My Post","content":"Hello World"}
                """;
        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("My Post"))
                .andExpect(jsonPath("$.authorUsername").value("alice"));
    }

    @Test
    void createPost_unauthenticated_returns401() throws Exception {
        String body = """
                {"title":"My Post","content":"Hello World"}
                """;
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_blankTitle_returns400() throws Exception {
        String body = """
                {"title":"","content":"Test"}
                """;
        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPosts_returns200() throws Exception {
        postRepository.save(PostEntity.builder()
                .title("Post 1").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        mockMvc.perform(get("/api/posts")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Post 1"));
    }

    @Test
    void getPostById_returns200() throws Exception {
        var saved = postRepository.save(PostEntity.builder()
                .title("Post 1").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        mockMvc.perform(get("/api/posts/" + saved.getId())
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Post 1"))
                .andExpect(jsonPath("$.authorId").value(alice.getId()));
    }

    @Test
    void getPostById_nonExistent_returns404() throws Exception {
        mockMvc.perform(get("/api/posts/999999")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePost_byOwner_returns200() throws Exception {
        var saved = postRepository.save(PostEntity.builder()
                .title("Original").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        String body = """
                {"title":"Updated Title","content":"Updated Content"}
                """;
        mockMvc.perform(put("/api/posts/" + saved.getId())
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updatePost_byNonOwner_returns403() throws Exception {
        var saved = postRepository.save(PostEntity.builder()
                .title("Alice's Post").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        String body = """
                {"title":"Hacked","content":"Hacked"}
                """;
        mockMvc.perform(put("/api/posts/" + saved.getId())
                        .header("Authorization", "Bearer " + bobToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePost_byOwner_returns204() throws Exception {
        var saved = postRepository.save(PostEntity.builder()
                .title("To Delete").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        mockMvc.perform(delete("/api/posts/" + saved.getId())
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_byAdmin_evenNotOwner_returns204() throws Exception {
        var saved = postRepository.save(PostEntity.builder()
                .title("Alice Post").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        mockMvc.perform(delete("/api/posts/" + saved.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_byNonOwner_returns403() throws Exception {
        var saved = postRepository.save(PostEntity.builder()
                .title("Alice Post").content("Content").author(aliceEntity).createdAt(Instant.now()).build());

        mockMvc.perform(delete("/api/posts/" + saved.getId())
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void pagination_returnsCorrectPage() throws Exception {
        for (int i = 0; i < 5; i++) {
            postRepository.save(PostEntity.builder()
                    .title("Post " + i).content("Content").author(aliceEntity).createdAt(Instant.now()).build());
        }

        mockMvc.perform(get("/api/posts?page=0&size=3")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/api/posts?page=1&size=3")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
