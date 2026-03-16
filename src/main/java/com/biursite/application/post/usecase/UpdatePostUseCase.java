package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.UpdatePostCommand;

public interface UpdatePostUseCase {
    void execute(UpdatePostCommand cmd);
}
