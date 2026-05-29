package com.biursite.application.query;

import com.biursite.application.query.dto.UserSummaryDto;

public interface GetUserQuery {
    UserSummaryDto execute(Long id);
}
