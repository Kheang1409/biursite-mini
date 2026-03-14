package com.biursite.service;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.dto.CreatePostRequest;
import com.biursite.dto.PostDTO;
import com.biursite.dto.UpdatePostRequest;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.entity.User;
import com.biursite.infrastructure.persistence.UserEntity;
import com.biursite.exception.ForbiddenException;
import com.biursite.exception.ResourceNotFoundException;
import com.biursite.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepositoryPort postRepository;
    @Mock private UserRepositoryPort userRepository;
    @Mock private DomainEventPublisher eventPublisher;

    @InjectMocks
    private PostServiceImpl postService;

    private User author;
    private UserEntity authorEntity;
    private Post testPost;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L).username("alice").email("alice@test.com")
                .password("enc").role(Role.ROLE_USER).createdAt(Instant.now())
                .build();
        authorEntity = UserEntity.builder()
            .id(author.getId())
            .username(author.getUsername())
            .email(author.getEmail())
            .password(author.getPassword())
            .role(author.getRole())
            .createdAt(author.getCreatedAt())
            .build();
        testPost = Post.builder()
            .id(10L).title("Test Title").content("Test content")
            .author(author).createdAt(Instant.now())
            .build();
    }

    @Test
    void getAll_returnsPaginatedDTOs() {
        Page<Post> page = new PageImpl<>(List.of(testPost));
        when(postRepository.findAllWithAuthorVisible(any(PageRequest.class))).thenReturn(page);

        List<PostDTO> result = postService.getAll(0, 20);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Title");
        assertThat(result.get(0).getAuthorUsername()).isEqualTo("alice");
    }

    @Test
    void getById_existing_returnsDTO() {
        when(postRepository.findByIdWithAuthor(10L)).thenReturn(Optional.of(testPost));
        PostDTO dto = postService.getById(10L);
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthorId()).isEqualTo(1L);
    }

    @Test
    void getById_nonExistent_throwsNotFound() {
        when(postRepository.findByIdWithAuthor(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_validRequest_returnsDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setId(11L);
            return p;
        });

        CreatePostRequest req = new CreatePostRequest("New Post", "Content here");
        PostDTO dto = postService.create(req, 1L);

        assertThat(dto.getTitle()).isEqualTo("New Post");
        verify(postRepository).save(any(Post.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    void create_nonExistentUser_throwsNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        CreatePostRequest req = new CreatePostRequest("Title", "Content");
        assertThatThrownBy(() -> postService.create(req, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_byOwner_updatesAndReturns() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        UpdatePostRequest req = new UpdatePostRequest("Updated Title", "Updated Content");
        PostDTO dto = postService.update(10L, req, 1L); // userId = 1 = author

        assertThat(dto.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_byNonOwner_throwsForbidden() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));

        UpdatePostRequest req = new UpdatePostRequest("Hacked", "Hacked");
        assertThatThrownBy(() -> postService.update(10L, req, 999L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Not allowed");
    }

    @Test
    void delete_byOwner_deletes() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        postService.delete(10L, 1L);
        verify(postRepository).delete(testPost);
    }

    @Test
    void delete_byAdmin_evenNotOwner_deletes() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        assertThatThrownBy(() -> postService.delete(10L, 999L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void delete_byNonOwnerNonAdmin_throwsForbidden() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(testPost));
        assertThatThrownBy(() -> postService.delete(10L, 999L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void toDto_mapsAllFields() {
        PostDTO dto = postService.toDto(testPost);
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getTitle()).isEqualTo("Test Title");
        assertThat(dto.getContent()).isEqualTo("Test content");
        assertThat(dto.getAuthorId()).isEqualTo(1L);
        assertThat(dto.getAuthorUsername()).isEqualTo("alice");
        assertThat(dto.getCreatedAt()).isNotNull();
    }
}
