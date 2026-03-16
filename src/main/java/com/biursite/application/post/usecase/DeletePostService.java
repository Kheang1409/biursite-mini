package com.biursite.application.post.usecase;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.event.PostDeletedEvent;
import com.biursite.domain.shared.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class DeletePostService implements DeletePostUseCase {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void execute(Long postId, Long currentUserId) {
        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAuthor = existing.getAuthor().getId().equals(currentUserId);

        if (!isAuthor) {
            var currentUserOpt = userRepository.findById(currentUserId);
            boolean isAdmin = currentUserOpt.isPresent() && currentUserOpt.get().getRole() == Role.ROLE_ADMIN;
            if (!isAdmin) throw new IllegalStateException("Not allowed to delete this post");
        }

        String title = existing.getTitle();
        postRepository.delete(existing);

        eventPublisher.publish(new PostDeletedEvent(postId, title, currentUserId));
    }
}
