package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllUsersUseCase {
    private final UserRepositoryPort userRepository;

    public GetAllUsersUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute() {
        return userRepository.findAll();
    }
}
