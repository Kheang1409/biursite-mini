package com.biursite.application.query;

import com.biursite.application.query.dto.PostDetailDto;
import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import java.time.Instant;
import java.util.List;

public interface PostQueryRepository {
    Page<PostSummaryDto> findPostList(String query, PageRequest pageRequest);
    List<PostSummaryDto> findPostListKeyset(String query, Instant beforeCreatedAt, Long beforeId, int size);
    PostDetailDto findPostDetail(Long id);
}
