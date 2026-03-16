package com.biursite.application.user.usecase;

import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.user.dto.UserProfileView;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetUserProfilePostsService implements GetUserProfilePostsUseCase {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final PostViewMapper postViewMapper;

    @Override
    public UserProfileView execute(Long userId, PageRequest pageRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<com.biursite.domain.post.entity.Post> all = postRepository.findByAuthorOrderByCreatedAtDesc(user);

        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<com.biursite.domain.post.entity.Post> slice = all.subList(from, to);

        var postViews = postViewMapper.toViewList(slice);

        UserProfileView profile = new UserProfileView();
        profile.setUsername(user.getUsername());
        profile.setBio(null);
        profile.setJoinedAt(user.getCreatedAt());
        profile.setPosts(postViews);
        // include email and role so templates can display them
        profile.setEmail(user.getEmail());
        profile.setRole(user.getRole() != null ? user.getRole().name() : null);
        profile.setDeactivated(user.getDeactivated());

        return profile;
    }
}
