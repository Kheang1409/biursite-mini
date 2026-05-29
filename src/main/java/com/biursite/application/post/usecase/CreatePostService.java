package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.post.event.PostCreatedEvent;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Transactional
public class CreatePostService implements CreatePostUseCase {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public Long execute(CreatePostCommand cmd) {
        var author = userRepository.findById(cmd.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", cmd.getAuthorId()));

        var post = Post.create(cmd.getTitle(), cmd.getContent(), author, Instant.now());

        var saved = postRepository.save(post);

        eventPublisher.publish(new PostCreatedEvent(saved.getId(), saved.getTitle(), author.getId(), author.getUsername()));

        return saved.getId();
    }
}
