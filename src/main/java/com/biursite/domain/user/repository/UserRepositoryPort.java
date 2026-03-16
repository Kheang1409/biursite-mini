package com.biursite.domain.user.repository;

import com.biursite.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findAll(int page, int size);
    long countAll();
    List<User> findAllWithFilter(String query, Boolean banned, int page, int size);
    long countAllWithFilter(String query, Boolean banned);
    User save(User user);
    void delete(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
