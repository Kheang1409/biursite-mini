package com.biursite.infrastructure.persistence.query;

import com.biursite.application.query.UserQueryRepository;
import com.biursite.application.query.dto.UserSummaryDto;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageImpl;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.infrastructure.persistence.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserQueryRepositoryAdapter implements UserQueryRepository {
    private final UserRepository userRepository;

    public UserQueryRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserSummaryDto> findUserPage(String query, Boolean banned, PageRequest pageRequest) {
        org.springframework.data.domain.Pageable spReq = org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        org.springframework.data.domain.Page<UserSummaryDto> springPage = userRepository.findUserListItems(query, banned, spReq);
        return new PageImpl<>(springPage.getContent(), springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
    }

    @Override
    public UserSummaryDto findUserById(Long id) {
        Optional<UserSummaryDto> view = userRepository.findUserListItem(id);
        return view.orElse(null);
    }
}
