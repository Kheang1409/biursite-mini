package com.biursite.infrastructure.web;

import com.biursite.application.post.dto.CreatePostCommand;
import com.biursite.application.post.dto.UpdatePostCommand;
import com.biursite.application.post.dto.PostView;
import com.biursite.application.post.usecase.CreatePostUseCase;
import com.biursite.application.post.usecase.DeletePostUseCase;
import com.biursite.application.query.GetPostQuery;
import com.biursite.application.query.ListPostsQuery;
import com.biursite.infrastructure.web.mapper.QueryDtoMapper;
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
    private final ListPostsQuery listPostsQuery;
    private final CreatePostUseCase createPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final GetPostQuery getPostQuery;
    private final CurrentUserPort currentUserPort;

    public PostPageControllerAdapter(ListPostsQuery listPostsQuery,
                                     CreatePostUseCase createPostUseCase,
                                     UpdatePostUseCase updatePostUseCase,
                                     DeletePostUseCase deletePostUseCase,
                                     GetPostQuery getPostQuery,
                                     CurrentUserPort currentUserPort) {
        this.listPostsQuery = listPostsQuery;
        this.createPostUseCase = createPostUseCase;
        this.updatePostUseCase = updatePostUseCase;
        this.deletePostUseCase = deletePostUseCase;
        this.getPostQuery = getPostQuery;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping({"/", "/posts"})
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model, java.security.Principal auth) {
        var result = listPostsQuery.execute(null, PageRequest.of(page, size));
        Page<PostView> postPage = new com.biursite.application.shared.pagination.PageImpl<>(
            result.getContent().stream().map(QueryDtoMapper::toPostView).toList(),
            result.getPageNumber(),
            result.getPageSize(),
            result.getTotalElements()
        );
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
        return QueryDtoMapper.toPostView(getPostQuery.execute(id));
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

        UpdatePostCommand cmd = new UpdatePostCommand(id, updatePostRequest.getTitle(), updatePostRequest.getContent(), currentUserPort.getCurrentUserId(), updatePostRequest.getVersion());
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
