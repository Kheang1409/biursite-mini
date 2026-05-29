package com.biursite.application.query;

import com.biursite.application.query.dto.UserSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;

public interface GetUserPageQuery {
    Page<UserSummaryDto> execute(String query, Boolean banned, PageRequest pageRequest);
}
