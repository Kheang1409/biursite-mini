package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.PostView;

public interface GetPostUseCase {
    PostView execute(Long id);
}
