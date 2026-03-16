package com.biursite.application.post.usecase;

import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePostServiceTest {
    @Mock
    PostRepositoryPort postRepository;
    @Mock
    UserRepositoryPort userRepository;
    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    DeletePostService service;

    @Test
    void shouldDeletePostWhenAuthor() {
        User author = User.builder().id(2L).build();
        Post p = Post.builder().id(1L).author(author).createdAt(Instant.now()).build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(p));

        service.execute(1L, 2L);

        verify(postRepository).delete(p);
        verify(eventPublisher).publish(any());
    }
}
