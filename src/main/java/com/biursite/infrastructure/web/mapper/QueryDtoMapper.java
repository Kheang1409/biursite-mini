package com.biursite.infrastructure.web.mapper;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.query.dto.PostDetailDto;
import com.biursite.application.query.dto.PostSummaryDto;
import com.biursite.application.query.dto.UserSummaryDto;
import com.biursite.application.user.dto.UserDto;

public final class QueryDtoMapper {
    private QueryDtoMapper() {}

    public static PostView toPostView(PostSummaryDto view) {
        if (view == null) {
            return null;
        }
        return new PostView(
                view.id(),
                null,
                view.title(),
                view.excerpt(),
                view.authorName(),
                null,
                view.createdAt(),
                null,
                null,
                null
        );
    }

    public static PostView toPostView(PostDetailDto view) {
        if (view == null) {
            return null;
        }
        return new PostView(
                view.id(),
                view.version(),
                view.title(),
                view.content(),
                view.authorName(),
                view.authorId(),
                view.createdAt(),
                view.updatedAt(),
                view.banned(),
                view.banReason()
        );
    }

    public static UserDto toUserDto(UserSummaryDto view) {
        if (view == null) {
            return null;
        }
        return UserDto.builder()
                .id(view.id())
                .version(view.version())
                .username(view.username())
                .email(view.email())
                .role(view.role() == null ? null : view.role().name())
                .banned(Boolean.TRUE.equals(view.banned()))
                .deactivated(Boolean.TRUE.equals(view.deactivated()))
                .createdAt(view.createdAt())
                .build();
    }
}
