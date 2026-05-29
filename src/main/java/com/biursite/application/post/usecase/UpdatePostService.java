package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.post.event.PostUpdatedEvent;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import com.biursite.domain.shared.exception.ConcurrencyConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Transactional
public class UpdatePostService implements UpdatePostUseCase {
    private final PostRepositoryPort postRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void execute(UpdatePostCommand cmd) {
        var existing = postRepository.findById(cmd.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", cmd.getPostId()));

        if (cmd.getVersion() != null && existing.getVersion() != null && !cmd.getVersion().equals(existing.getVersion())) {
            throw new ConcurrencyConflictException("Post was updated by another request");
        }

        existing.updateBy(cmd.getCurrentUserId(), cmd.getTitle(), cmd.getContent(), Instant.now());

        var saved = saveWithConflictHandling(existing, "Post was updated by another request");

        eventPublisher.publish(new PostUpdatedEvent(saved.getId(), saved.getTitle(), cmd.getCurrentUserId()));
    }

    private com.biursite.domain.post.entity.Post saveWithConflictHandling(com.biursite.domain.post.entity.Post post, String message) {
        try {
            return postRepository.save(post);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException ex) {
            throw new ConcurrencyConflictException(message);
        }
    }
}
