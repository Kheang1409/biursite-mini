package com.biursite.application.query;

import com.biursite.application.query.dto.PostDetailDto;

public interface GetPostQuery {
    PostDetailDto execute(Long id);
}
