package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPostServiceTest {
    @Mock
    PostRepositoryPort postRepository;

    @Mock
    PostViewMapper postViewMapper;

    @InjectMocks
    GetPostService service;

    @Test
    void shouldReturnPostView() {
        User author = User.builder().id(2L).username("alice").build();
        Post p = Post.builder().id(1L).title("t").content("c").author(author).createdAt(Instant.now()).build();
        when(postRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(p));
        when(postViewMapper.toView(p)).thenReturn(new PostView(p.getId(), p.getTitle(), p.getContent(), p.getAuthor().getUsername(), p.getAuthor().getId(), p.getCreatedAt(), p.getUpdatedAt()));

        PostView view = service.execute(1L);

        assertNotNull(view);
        assertEquals("t", view.getTitle());
        assertEquals("alice", view.getAuthorUsername());
    }
}
