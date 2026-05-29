package com.biursite.application.query;

import com.biursite.application.query.dto.UserSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class GetUserPageQueryService implements GetUserPageQuery {
    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryDto> execute(String query, Boolean banned, PageRequest pageRequest) {
        return userQueryRepository.findUserPage(query, banned, pageRequest);
    }
}
