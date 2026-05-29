package com.biursite.application.query;

import com.biursite.application.query.dto.UserSummaryDto;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class GetUserQueryService implements GetUserQuery {
    private final UserQueryRepository userQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDto execute(Long id) {
        UserSummaryDto view = userQueryRepository.findUserById(id);
        if (view == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return view;
    }
}
