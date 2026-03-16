package com.biursite.application.user.usecase;

import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.user.dto.UserDto;

public interface GetUserPageUseCase {
    Page<UserDto> execute(String query, Boolean banned, PageRequest pageRequest);
}
