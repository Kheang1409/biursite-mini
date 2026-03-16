package com.biursite.domain.post.repository;

import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    Optional<Post> findById(Long id);
    Optional<Post> findByIdWithAuthor(Long id);
    List<Post> findAll();
    List<Post> findAllWithAuthor(int page, int size);
    long countAllWithAuthor();
    List<Post> findAllWithAuthorVisible(int page, int size);
    long countAllWithAuthorVisible();
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
    Post save(Post post);
    void delete(Post post);
}
