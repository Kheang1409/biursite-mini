package com.biursite.controller.web;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.dto.CreatePostRequest;
import com.biursite.dto.UpdatePostRequest;
import com.biursite.domain.post.entity.Post;
import com.biursite.domain.user.entity.User;
// persistence imports removed after Post domain/persistence refactor
import com.biursite.exception.ResourceNotFoundException;
import com.biursite.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Controller
public class PostPageController {
    private final PostRepositoryPort postRepository;
    private final UserRepositoryPort userRepository;
    private final PostService postService;

    public PostPageController(PostRepositoryPort postRepository,
                               UserRepositoryPort userRepository,
                               PostService postService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
    }

    @GetMapping({"/", "/posts"})
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model, Authentication auth) {
                    Page<Post> postPage = postRepository.findAllWithAuthorVisible(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("createPostRequest", new CreatePostRequest());
        
        if (auth != null) {
            model.addAttribute("currentUsername", auth.getName());
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return "posts/list";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model, Authentication auth) {
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/json")
    @ResponseBody
    public Object getPostJson(@PathVariable Long id) {
        return postService.getById(id);
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("createPostRequest", new CreatePostRequest());
        return "posts/form";
    }

    @PostMapping("/posts/new")
    public String createPost(@Valid @ModelAttribute CreatePostRequest createPostRequest,
                             BindingResult result, Authentication auth) {
        if (result.hasErrors()) {
            return "posts/form";
        }
        
        User user = userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new IllegalStateException("User not found"));

        Post post = Post.builder()
            .title(createPostRequest.getTitle())
            .content(createPostRequest.getContent())
            .author(user)
            .createdAt(Instant.now())
            .build();
        
        Post saved = postRepository.save(post);
        return "redirect:/posts/" + saved.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, Authentication auth) {
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @Valid @ModelAttribute UpdatePostRequest updatePostRequest,
                             BindingResult result, Authentication auth, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("postId", id);
            return "posts/edit";
        }
        
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
            if (!post.getAuthor().getUsername().equals(auth.getName())) {
                return "redirect:/posts?forbidden";
            }
        
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, Authentication auth) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
            if (!post.getAuthor().getUsername().equals(auth.getName())) {
                return "redirect:/posts?forbidden";
            }
        
        postRepository.delete(post);
        return "redirect:/posts?deleted";
    }
}
