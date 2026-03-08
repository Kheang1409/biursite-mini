package com.biursite.controller;

import com.biursite.dto.CreatePostRequest;
import com.biursite.dto.PostDTO;
import com.biursite.entity.Post;
import com.biursite.entity.User;
import com.biursite.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> all() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody CreatePostRequest req, Authentication auth) {
        User u = User.builder().id(null).username(auth.getName()).build();
        Post p = Post.builder().title(req.getTitle()).content(req.getContent()).author(u).build();
        // We'll need author id; controller expects JWT username -> lookup in service when creating
        return ResponseEntity.status(201).body(postService.create(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id, @Valid @RequestBody CreatePostRequest req, Authentication auth) {
        // pass current user id as null for now; PostService will validate ownership using username lookup in a fuller impl
        Post p = Post.builder().title(req.getTitle()).content(req.getContent()).build();
        // For simplicity, treat username as id lookup is required; returning 200 if allowed
        return ResponseEntity.ok(postService.update(id, p, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        // similarly, deletion will require user context
        postService.delete(id, null, auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return ResponseEntity.noContent().build();
    }
}
