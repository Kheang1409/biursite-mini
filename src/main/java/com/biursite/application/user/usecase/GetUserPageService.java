package com.biursite.application.user.usecase;

import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.shared.pagination.SimplePage;
import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class GetUserPageService implements GetUserPageUseCase {
    private final UserRepositoryPort userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public Page<UserDto> execute(String query, Boolean banned, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        var domainList = userRepository.findAllWithFilter(query, banned, page, size);
        long total = userRepository.countAllWithFilter(query, banned);
        var dtoList = domainList.stream().map(userDtoMapper::toDto).toList();
        int totalPages = size == 0 ? 0 : (int) ((total + size - 1) / size);
        return new SimplePage<>(dtoList, page, size, total, totalPages);
    }
}
