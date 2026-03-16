package com.biursite.application.post.usecase;

public interface DeletePostUseCase {
    void execute(Long postId, Long currentUserId);
}
