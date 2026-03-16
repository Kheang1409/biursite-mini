package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
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
class UpdatePostServiceTest {
    @Mock
    PostRepositoryPort postRepository;
    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    UpdatePostService service;

    @Test
    void shouldUpdatePostAndPublishEvent() {
        Post existing = Post.builder().id(1L).title("old").content("c").author(null).createdAt(Instant.now()).build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(existing));

        Post saved = Post.builder().id(1L).title("new").content("c2").createdAt(Instant.now()).build();
        when(postRepository.save(existing)).thenReturn(saved);

        UpdatePostCommand cmd = new UpdatePostCommand(1L, "new", "c2", null);
        service.execute(cmd);

        verify(postRepository).save(existing);
        verify(eventPublisher).publish(any());
    }
}
