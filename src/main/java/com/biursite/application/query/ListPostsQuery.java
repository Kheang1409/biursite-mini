package com.biursite.application.query;

import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;

public interface ListPostsQuery {
    Page<PostSummaryDto> execute(String query, PageRequest pageRequest);
}
