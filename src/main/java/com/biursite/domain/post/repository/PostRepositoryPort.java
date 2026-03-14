package com.biursite.domain.post.repository;

import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    Optional<Post> findById(Long id);
    Optional<Post> findByIdWithAuthor(Long id);
    List<Post> findAll();
    Page<Post> findAllWithAuthor(Pageable pageable);
    Page<Post> findAllWithAuthorVisible(Pageable pageable);
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
    Post save(Post post);
    void delete(Post post);
}
