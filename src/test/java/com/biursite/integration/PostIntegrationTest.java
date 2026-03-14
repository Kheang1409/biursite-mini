package com.biursite.integration;

import com.biursite.infrastructure.persistence.PostEntity;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.entity.User;
import com.biursite.infrastructure.persistence.PostRepository;
import com.biursite.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createAndListPost_persistsBannedFlag() throws Exception {
        long before = postRepository.count();

        // Create a test user to satisfy non-null author FK
        com.biursite.infrastructure.persistence.UserEntity uEntity = com.biursite.infrastructure.persistence.UserEntity.builder()
            .username("postuser")
            .email("postuser@example.com")
            .password("pass")
            .role(Role.ROLE_USER)
            .createdAt(Instant.now())
            .banned(false)
            .build();
        var savedEntity = userRepository.save(uEntity);
        User savedUser = com.biursite.domain.user.entity.User.builder()
            .id(savedEntity.getId())
            .username(savedEntity.getUsername())
            .email(savedEntity.getEmail())
            .password(savedEntity.getPassword())
            .role(savedEntity.getRole())
            .createdAt(savedEntity.getCreatedAt())
            .banned(savedEntity.getBanned())
            .build();

        // Create a post via repository directly (use persistence entity)
        PostEntity p = PostEntity.builder()
            .title("Integration Test Post")
            .content("Content")
            .author(savedEntity)
            .createdAt(Instant.now())
            .banned(false)
            .build();
        var saved = postRepository.save(p);

        assertThat(postRepository.findById(saved.getId())).isPresent();
        assertThat(saved.getBanned()).isFalse();

        // Ensure list endpoint returns 200
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk());

        long after = postRepository.count();
        assertThat(after).isGreaterThanOrEqualTo(before + 1);
    }
}
