package com.biursite.controller;

import com.biursite.dto.CreatePostRequest;
import com.biursite.dto.PostDTO;
import com.biursite.dto.UpdatePostRequest;
import com.biursite.security.SecurityService;
import com.biursite.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final SecurityService securityService;

    public PostController(PostService postService, SecurityService securityService) {
        this.postService = postService;
        this.securityService = securityService;
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> all(@RequestParam(defaultValue = "0") int page, 
                                              @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody CreatePostRequest req, Authentication auth) {
        Long currentUserId = securityService.getCurrentUserId();
        return ResponseEntity.status(201).body(postService.create(req, currentUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id, 
                                          @Valid @RequestBody UpdatePostRequest req, 
                                          Authentication auth) {
        Long currentUserId = securityService.getCurrentUserId();
        return ResponseEntity.ok(postService.update(id, req, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long currentUserId = securityService.getCurrentUserId();
        postService.delete(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
