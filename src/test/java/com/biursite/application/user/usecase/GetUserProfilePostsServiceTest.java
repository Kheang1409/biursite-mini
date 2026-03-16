package com.biursite.application.user.usecase;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.application.user.dto.UserProfileView;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfilePostsServiceTest {
    @Mock
    PostRepositoryPort postRepository;
    @Mock
    UserRepositoryPort userRepository;
    @Mock
    PostViewMapper postViewMapper;

    @InjectMocks
    GetUserProfilePostsService service;

    @Test
    void shouldReturnUserProfileWithPosts() {
        User u = User.builder().id(3L).username("u").createdAt(Instant.now()).build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(u));

        Post p = Post.builder().id(5L).title("t").author(u).createdAt(Instant.now()).build();
        when(postRepository.findByAuthorOrderByCreatedAtDesc(u)).thenReturn(List.of(p));

        when(postViewMapper.toViewList(org.mockito.ArgumentMatchers.anyList())).thenReturn(List.of(new PostView(p.getId(), p.getTitle(), p.getContent(), u.getUsername(), u.getId(), p.getCreatedAt(), p.getUpdatedAt())));

        UserProfileView profile = service.execute(3L, PageRequest.of(0,10));
        assertNotNull(profile);
        assertEquals("u", profile.getUsername());
        assertEquals(1, profile.getPosts().size());
    }
}
