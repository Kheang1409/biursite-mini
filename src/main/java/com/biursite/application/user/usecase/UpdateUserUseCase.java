package com.biursite.application.user.usecase;

import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.dto.UserDto;

public interface UpdateUserUseCase {
    UserDto execute(Long id, UpdateUserRequest request);
}
