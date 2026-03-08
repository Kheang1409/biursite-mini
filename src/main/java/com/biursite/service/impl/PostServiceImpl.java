package com.biursite.service.impl;

import com.biursite.dto.PostDTO;
import com.biursite.entity.Post;
import com.biursite.entity.User;
import com.biursite.exception.ResourceNotFoundException;
import com.biursite.repository.PostRepository;
import com.biursite.repository.UserRepository;
import com.biursite.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
    public List<PostDTO> getAll() {
        return postRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public PostDTO getById(Long id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return toDto(p);
    }

    @Override
    public PostDTO create(Post post) {
        User author = userRepository.findById(post.getAuthor().getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", post.getAuthor().getId()));
        post.setAuthor(author);
        post.setCreatedAt(Instant.now());
        Post saved = postRepository.save(post);
        return toDto(saved);
    }

    @Override
    public PostDTO update(Long id, Post post, Long currentUserId) {
        Post existing = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        if (!existing.getAuthor().getId().equals(currentUserId)) {
            throw new SecurityException("Not allowed to edit this post");
        }
        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        existing.setUpdatedAt(Instant.now());
        Post saved = postRepository.save(existing);
        return toDto(saved);
    }

    @Override
    public void delete(Long id, Long currentUserId, boolean isAdmin) {
        Post existing = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        if (!isAdmin && !existing.getAuthor().getId().equals(currentUserId)) {
            throw new SecurityException("Not allowed to delete this post");
        }
        postRepository.delete(existing);
    }
}
