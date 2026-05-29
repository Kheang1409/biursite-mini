package com.biursite.application.query;

import com.biursite.application.query.dto.PostDetailDto;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class GetPostQueryService implements GetPostQuery {
    private final PostQueryRepository postQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public PostDetailDto execute(Long id) {
        PostDetailDto view = postQueryRepository.findPostDetail(id);
        if (view == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        return view;
    }
}
