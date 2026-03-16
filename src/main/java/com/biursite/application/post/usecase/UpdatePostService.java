package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.post.event.PostUpdatedEvent;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class UpdatePostService implements UpdatePostUseCase {
    private final PostRepositoryPort postRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void execute(UpdatePostCommand cmd) {
        var existing = postRepository.findById(cmd.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (existing.getAuthor() == null) {
            if (cmd.getCurrentUserId() != null) {
                throw new IllegalStateException("Not allowed to edit this post");
            }
        } else {
            if (!existing.getAuthor().getId().equals(cmd.getCurrentUserId())) {
                throw new IllegalStateException("Not allowed to edit this post");
            }
        }

        existing.setTitle(cmd.getTitle());
        existing.setContent(cmd.getContent());
        existing.setUpdatedAt(Instant.now());

        var saved = postRepository.save(existing);

        eventPublisher.publish(new PostUpdatedEvent(saved.getId(), saved.getTitle(), cmd.getCurrentUserId()));
    }
}
