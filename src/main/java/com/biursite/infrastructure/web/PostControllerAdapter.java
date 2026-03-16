package com.biursite.infrastructure.web;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.infrastructure.web.dto.CreatePostRequest;
import com.biursite.infrastructure.web.dto.UpdatePostRequest;
import jakarta.validation.Valid;
import com.biursite.application.post.usecase.CreatePostUseCase;
import com.biursite.application.post.usecase.DeletePostUseCase;
import com.biursite.application.post.usecase.GetPostUseCase;
import com.biursite.application.post.usecase.ListPostsUseCase;
import com.biursite.application.post.usecase.UpdatePostUseCase;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.shared.security.CurrentUserPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostControllerAdapter {
    private final ListPostsUseCase listPostsUseCase;
    private final GetPostUseCase getPostUseCase;
    private final CreatePostUseCase createPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final CurrentUserPort currentUserPort;

    public PostControllerAdapter(ListPostsUseCase listPostsUseCase,
                                 GetPostUseCase getPostUseCase,
                                 CreatePostUseCase createPostUseCase,
                                 UpdatePostUseCase updatePostUseCase,
                                 DeletePostUseCase deletePostUseCase,
                                 CurrentUserPort currentUserPort) {
        this.listPostsUseCase = listPostsUseCase;
        this.getPostUseCase = getPostUseCase;
        this.createPostUseCase = createPostUseCase;
        this.updatePostUseCase = updatePostUseCase;
        this.deletePostUseCase = deletePostUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping
    public ResponseEntity<java.util.List<PostView>> all(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        var result = listPostsUseCase.execute(PageRequest.of(page, size));
        return ResponseEntity.ok(result.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostView> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getPostUseCase.execute(id));
    }

    @PostMapping
    public ResponseEntity<PostView> create(@Valid @RequestBody CreatePostRequest req) {
        Long authorId = currentUserPort.getCurrentUserId();
        var cmd = new CreatePostCommand(req.getTitle(), req.getContent(), authorId);
        var id = createPostUseCase.execute(cmd);
        var created = getPostUseCase.execute(id);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostView> update(@PathVariable Long id, @Valid @RequestBody UpdatePostRequest req) {
        Long currentUserId = currentUserPort.getCurrentUserId();
        try {
            updatePostUseCase.execute(new UpdatePostCommand(id, req.getTitle(), req.getContent(), currentUserId));
            var updated = getPostUseCase.execute(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            deletePostUseCase.execute(id, currentUserPort.getCurrentUserId());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(403).build();
        }
    }
}
