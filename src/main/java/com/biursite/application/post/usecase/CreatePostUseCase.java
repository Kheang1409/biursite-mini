package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.CreatePostCommand;

public interface CreatePostUseCase {
    Long execute(CreatePostCommand cmd);
}
