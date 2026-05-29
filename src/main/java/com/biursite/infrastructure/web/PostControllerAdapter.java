package com.biursite.infrastructure.web;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.application.query.GetPostQuery;
import com.biursite.application.query.ListPostsQuery;
import com.biursite.infrastructure.web.dto.CreatePostRequest;
import com.biursite.infrastructure.web.dto.UpdatePostRequest;
import com.biursite.infrastructure.web.mapper.QueryDtoMapper;
import jakarta.validation.Valid;
import com.biursite.application.post.usecase.CreatePostUseCase;
import com.biursite.application.post.usecase.DeletePostUseCase;
import com.biursite.application.post.usecase.UpdatePostUseCase;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.shared.security.CurrentUserPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostControllerAdapter {
    private final ListPostsQuery listPostsQuery;
    private final GetPostQuery getPostQuery;
    private final CreatePostUseCase createPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final CurrentUserPort currentUserPort;

    public PostControllerAdapter(ListPostsQuery listPostsQuery,
                                 GetPostQuery getPostQuery,
                                 CreatePostUseCase createPostUseCase,
                                 UpdatePostUseCase updatePostUseCase,
                                 DeletePostUseCase deletePostUseCase,
                                 CurrentUserPort currentUserPort) {
        this.listPostsQuery = listPostsQuery;
        this.getPostQuery = getPostQuery;
        this.createPostUseCase = createPostUseCase;
        this.updatePostUseCase = updatePostUseCase;
        this.deletePostUseCase = deletePostUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    public ResponseEntity<List<PostView>> all(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String q,
                                              HttpServletRequest request) {
        var result = listPostsQuery.execute(q, PageRequest.of(page, size));
        List<PostView> content = result.getContent().stream().map(QueryDtoMapper::toPostView).toList();
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostView> getById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(QueryDtoMapper.toPostView(getPostQuery.execute(id)));
    }

    @PostMapping
    public ResponseEntity<PostView> create(@Valid @RequestBody CreatePostRequest req, HttpServletRequest request) {
        Long authorId = currentUserPort.getCurrentUserId();
        var cmd = new CreatePostCommand(req.getTitle(), req.getContent(), authorId);
        var id = createPostUseCase.execute(cmd);
        var created = QueryDtoMapper.toPostView(getPostQuery.execute(id));
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostView> update(@PathVariable Long id, @Valid @RequestBody UpdatePostRequest req, HttpServletRequest request) {
        Long currentUserId = currentUserPort.getCurrentUserId();
        updatePostUseCase.execute(new UpdatePostCommand(id, req.getTitle(), req.getContent(), currentUserId, req.getVersion()));
        var updated = QueryDtoMapper.toPostView(getPostQuery.execute(id));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        deletePostUseCase.execute(id, currentUserPort.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
