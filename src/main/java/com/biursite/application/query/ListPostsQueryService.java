package com.biursite.application.query;

import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ListPostsQueryService implements ListPostsQuery {
    private final PostQueryRepository postQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> execute(String query, PageRequest pageRequest) {
        return postQueryRepository.findPostList(query, pageRequest);
    }
}
