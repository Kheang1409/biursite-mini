package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.application.user.dto.CreateUserCommand;

public interface RegisterUserUseCase {
    User execute(CreateUserCommand cmd);
}
