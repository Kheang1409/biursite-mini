package com.biursite.service.impl;

import com.biursite.domain.post.event.PostCreatedEvent;
import com.biursite.domain.post.event.PostDeletedEvent;
import com.biursite.domain.post.event.PostUpdatedEvent;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.dto.CreatePostRequest;
import com.biursite.dto.PostDTO;
import com.biursite.dto.UpdatePostRequest;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.entity.User;
import com.biursite.exception.ResourceNotFoundException;
import com.biursite.exception.ForbiddenException;
import com.biursite.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final DomainEventPublisher eventPublisher;

    public PostServiceImpl(PostRepositoryPort postRepository, 
                           UserRepositoryPort userRepository,
                           DomainEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PostDTO toDto(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getAll(int page, int size) {
                return postRepository.findAllWithAuthorVisible(PageRequest.of(page, size))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO getById(Long id) {
        Post post = postRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return toDto(post);
    }

    @Override
    public PostDTO create(CreatePostRequest req, Long currentUserId) {
        User authorDomain = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Post post = Post.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .author(authorDomain)
                .createdAt(Instant.now())
                .build();
        
        Post saved = postRepository.save(post);
        
        eventPublisher.publish(new PostCreatedEvent(
                saved.getId(),
                saved.getTitle(),
                authorDomain.getId(),
                authorDomain.getUsername()
        ));
        
        return toDto(saved);
    }

    @Override
    public PostDTO update(Long id, UpdatePostRequest req, Long currentUserId) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        if (!existing.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not allowed to edit this post");
        }
        
        existing.setTitle(req.getTitle());
        existing.setContent(req.getContent());
        existing.setUpdatedAt(Instant.now());
        
        Post saved = postRepository.save(existing);
        
        eventPublisher.publish(new PostUpdatedEvent(
                saved.getId(),
                saved.getTitle(),
                currentUserId
        ));
        
        return toDto(saved);
    }

    @Override
        public void delete(Long id, Long currentUserId) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

                // Allow the author to delete without fetching the user; otherwise require admin
                boolean isAuthor = existing.getAuthor().getId().equals(currentUserId);

                if (!isAuthor) {
                        // If the user is missing or not an admin, treat as forbidden for delete
                        var currentUserOpt = userRepository.findById(currentUserId);
                        boolean isAdmin = currentUserOpt.isPresent() && currentUserOpt.get().getRole() == Role.ROLE_ADMIN;
                        if (!isAdmin) {
                                throw new ForbiddenException("Not allowed to delete this post");
                        }
                }

                String title = existing.getTitle();
                postRepository.delete(existing);

        eventPublisher.publish(new PostDeletedEvent(
                id,
                title,
                currentUserId
        ));
        }
}
