package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePostServiceTest {
    @Mock
    PostRepositoryPort postRepository;
    @Mock
    UserRepositoryPort userRepository;
    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    CreatePostService service;

    @Test
    void shouldCreatePostAndPublishEvent() {
        User u = User.builder().id(5L).username("bob").build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(u));

        Post saved = Post.builder().id(10L).title("t").content("c").author(u).createdAt(Instant.now()).build();
        when(postRepository.save(any())).thenReturn(saved);

        CreatePostCommand cmd = new CreatePostCommand("t","c",5L);
        Long resultId = service.execute(cmd);

        assertNotNull(resultId);
        assertEquals(10L, resultId);
        verify(eventPublisher).publish(any());
    }
}
