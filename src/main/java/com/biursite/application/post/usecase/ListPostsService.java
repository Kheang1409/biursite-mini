package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageImpl;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.domain.post.repository.PostRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListPostsService implements ListPostsUseCase {
    private final PostRepositoryPort postRepository;
    private final PostViewMapper postViewMapper;

    @Override
    public Page<PostView> execute(PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        List<com.biursite.domain.post.entity.Post> domainList = postRepository.findAllWithAuthorVisible(page, size);
        long total = postRepository.countAllWithAuthorVisible();
        List<PostView> content = domainList.stream().map(postViewMapper::toView).toList();
        return new PageImpl<>(content, page, size, total);
    }
}
