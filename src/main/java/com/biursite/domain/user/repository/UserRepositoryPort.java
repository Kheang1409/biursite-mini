package com.biursite.domain.user.repository;

import com.biursite.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Page<User> findAllWithFilter(String query, Boolean banned, Pageable pageable);
    User save(User user);
    void delete(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
