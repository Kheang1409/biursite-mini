package com.biursite.infrastructure.web;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.usecase.CreatePostUseCase;
import com.biursite.application.post.usecase.DeletePostUseCase;
import com.biursite.application.post.usecase.GetPostUseCase;
import com.biursite.application.post.usecase.ListPostsUseCase;
import com.biursite.application.post.usecase.UpdatePostUseCase;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.shared.security.CurrentUserPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.biursite.infrastructure.web.dto.CreatePostRequest;
import com.biursite.infrastructure.web.dto.UpdatePostRequest;

import jakarta.validation.Valid;

@Controller
public class PostPageControllerAdapter {
    private final ListPostsUseCase listPostsUseCase;
    private final CreatePostUseCase createPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final GetPostUseCase getPostUseCase;
    private final CurrentUserPort currentUserPort;

    public PostPageControllerAdapter(ListPostsUseCase listPostsUseCase,
                                     CreatePostUseCase createPostUseCase,
                                     UpdatePostUseCase updatePostUseCase,
                                     DeletePostUseCase deletePostUseCase,
                                     GetPostUseCase getPostUseCase,
                                     CurrentUserPort currentUserPort) {
        this.listPostsUseCase = listPostsUseCase;
        this.createPostUseCase = createPostUseCase;
        this.updatePostUseCase = updatePostUseCase;
        this.deletePostUseCase = deletePostUseCase;
        this.getPostUseCase = getPostUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping({"/", "/posts"})
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model, java.security.Principal auth) {
        Page<PostView> postPage = listPostsUseCase.execute(PageRequest.of(page, size));
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("createPostRequest", new CreatePostRequest());

        if (auth != null) {
            model.addAttribute("currentUsername", auth.getName());
        }
        return "posts/list";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model, java.security.Principal auth) {
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/json")
    @ResponseBody
    public PostView getPostJson(@PathVariable Long id) {
        return getPostUseCase.execute(id);
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("createPostRequest", new CreatePostRequest());
        return "posts/form";
    }

    @PostMapping("/posts/new")
    public String createPost(@Valid @ModelAttribute CreatePostRequest createPostRequest,
                             BindingResult result, java.security.Principal auth) {
        if (result.hasErrors()) {
            return "posts/form";
        }

        Long authorId = currentUserPort.getCurrentUserId();
        CreatePostCommand cmd = new CreatePostCommand(createPostRequest.getTitle(), createPostRequest.getContent(), authorId);
        Long savedId = createPostUseCase.execute(cmd);
        return "redirect:/posts/" + savedId;
    }

    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, java.security.Principal auth) {
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @Valid @ModelAttribute UpdatePostRequest updatePostRequest,
                             BindingResult result, java.security.Principal auth, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("postId", id);
            return "posts/edit";
        }

        UpdatePostCommand cmd = new UpdatePostCommand(id, updatePostRequest.getTitle(), updatePostRequest.getContent(), currentUserPort.getCurrentUserId());
        try {
            updatePostUseCase.execute(cmd);
        } catch (IllegalStateException ex) {
            return "redirect:/posts?forbidden";
        }

        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, java.security.Principal auth) {
        try {
            deletePostUseCase.execute(id, currentUserPort.getCurrentUserId());
        } catch (IllegalStateException ex) {
            return "redirect:/posts?forbidden";
        }
        return "redirect:/posts?deleted";
    }
}
