package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class GetPostService implements GetPostUseCase {
    private final PostRepositoryPort postRepository;
    private final PostViewMapper postViewMapper;

    @Override
    public PostView execute(Long id) {
        var post = postRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        if (Boolean.TRUE.equals(post.getBanned()) || (post.getAuthor() != null && Boolean.TRUE.equals(post.getAuthor().getDeactivated()))) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        return postViewMapper.toView(post);
    }
}
