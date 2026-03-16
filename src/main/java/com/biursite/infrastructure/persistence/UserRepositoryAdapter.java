package com.biursite.infrastructure.persistence;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserRepository userRepository;

    public UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserEntityMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream().map(UserEntityMapper::toDomain).toList();
    }

    @Override
    public List<User> findAll(int page, int size) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<UserEntity> springPage = userRepository.findAll(spReq);
        return springPage.stream().map(UserEntityMapper::toDomain).toList();
    }

    @Override
    public long countAll() {
        return userRepository.count();
    }

    @Override
    public List<User> findAllWithFilter(String query, Boolean banned, int page, int size) {
        org.springframework.data.jpa.domain.Specification<UserEntity> spec = (root, cq, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> preds = new java.util.ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate p1 = cb.like(cb.lower(root.get("username")), like);
                jakarta.persistence.criteria.Predicate p2 = cb.like(cb.lower(root.get("email")), like);
                preds.add(cb.or(p1, p2));
            }
            if (banned != null) {
                preds.add(cb.equal(root.get("banned"), banned));
            }
            return preds.isEmpty() ? cb.conjunction() : cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<UserEntity> springPage = userRepository.findAll(spec, spReq);
        return springPage.stream().map(UserEntityMapper::toDomain).toList();
    }

    @Override
    public long countAllWithFilter(String query, Boolean banned) {
        org.springframework.data.jpa.domain.Specification<UserEntity> spec = (root, cq, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> preds = new java.util.ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate p1 = cb.like(cb.lower(root.get("username")), like);
                jakarta.persistence.criteria.Predicate p2 = cb.like(cb.lower(root.get("email")), like);
                preds.add(cb.or(p1, p2));
            }
            if (banned != null) {
                preds.add(cb.equal(root.get("banned"), banned));
            }
            return preds.isEmpty() ? cb.conjunction() : cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return userRepository.count(spec);
    }

    @Override
    @Transactional
    public User save(User user) {
        // ensure createdAt default
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now());
        }
        org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserRepositoryAdapter.class);
        LOG.info("Saving user id={} username={} deactivated={}", user.getId(), user.getUsername(), user.getDeactivated());
        UserEntity ent = UserEntityMapper.toEntity(user);
        UserEntity saved = userRepository.save(ent);
        LOG.info("User persisted id={} username={} deactivated={}", saved.getId(), saved.getUsername(), saved.getDeactivated());
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(User user) {
        if (user == null) return;
        if (user.getId() != null) {
            userRepository.deleteById(user.getId());
            return;
        }
        UserEntity ent = UserEntityMapper.toEntity(user);
        userRepository.delete(ent);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
