package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GetUserPageUseCase {
    private final UserRepositoryPort userRepository;

    public GetUserPageUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> execute(String query, Boolean banned, Pageable pageable) {
        return userRepository.findAllWithFilter(query, banned, pageable);
    }
}
