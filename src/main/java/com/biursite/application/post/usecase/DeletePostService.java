package com.biursite.application.post.usecase;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.event.PostDeletedEvent;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
@RequiredArgsConstructor
@Transactional
public class DeletePostService implements DeletePostUseCase {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void execute(Long postId, Long currentUserId) {
        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        boolean isAdmin = userRepository.findById(currentUserId)
            .map(user -> user.getRole() == Role.ROLE_ADMIN)
            .orElse(false);
        existing.deleteBy(currentUserId, isAdmin);

        String title = existing.getTitle();
        postRepository.delete(existing);

        eventPublisher.publish(new PostDeletedEvent(postId, title, currentUserId));
    }
}
