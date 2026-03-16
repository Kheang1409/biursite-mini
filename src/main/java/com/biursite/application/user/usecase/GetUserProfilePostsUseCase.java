package com.biursite.application.user.usecase;

import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.user.dto.UserProfileView;

public interface GetUserProfilePostsUseCase {
    UserProfileView execute(Long userId, PageRequest pageRequest);
}
