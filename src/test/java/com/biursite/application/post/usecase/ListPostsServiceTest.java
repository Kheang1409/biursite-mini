package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.mapper.PostViewMapper;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageImpl;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.post.repository.PostRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPostsServiceTest {
    @Mock
    PostRepositoryPort postRepository;

    @Mock
    PostViewMapper postViewMapper;

    @InjectMocks
    ListPostsService service;

    @Test
    void shouldReturnPagedPostViews() {
        Post p = Post.builder().id(1L).title("t").content("c").createdAt(Instant.now()).build();
        var domainList = List.of(p);
        when(postRepository.findAllWithAuthorVisible(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(domainList);
        when(postRepository.countAllWithAuthorVisible()).thenReturn(1L);
        when(postViewMapper.toView(p)).thenReturn(new PostView(p.getId(), p.getTitle(), p.getContent(), null, null, p.getCreatedAt(), p.getUpdatedAt()));

        Page<PostView> result = service.execute(PageRequest.of(0,10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("t", result.getContent().get(0).getTitle());
    }
}
