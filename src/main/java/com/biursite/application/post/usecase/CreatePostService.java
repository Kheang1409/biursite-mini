package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.post.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class CreatePostService implements CreatePostUseCase {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public Long execute(CreatePostCommand cmd) {
        var author = userRepository.findById(cmd.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var post = Post.builder()
                .title(cmd.getTitle())
                .content(cmd.getContent())
                .author(author)
                .createdAt(Instant.now())
                .build();

        var saved = postRepository.save(post);

        eventPublisher.publish(new PostCreatedEvent(saved.getId(), saved.getTitle(), author.getId(), author.getUsername()));

        return saved.getId();
    }
}
