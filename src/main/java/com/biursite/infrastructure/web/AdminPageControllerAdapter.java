package com.biursite.infrastructure.web;

import com.biursite.application.user.usecase.GetUserPageUseCase;
import com.biursite.application.user.usecase.GetUserByIdUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.application.shared.pagination.PageRequest;
import com.biursite.application.shared.security.CurrentUserPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.biursite.infrastructure.web.dto.UserChunkResponse;

@Controller
@RequestMapping("/admin")
public class AdminPageControllerAdapter {
    private static final int CHUNK_SIZE = 10;

    private final GetUserPageUseCase getUserPageUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases;
    private final CurrentUserPort currentUserPort;

    public AdminPageControllerAdapter(GetUserPageUseCase getUserPageUseCase,
                                      GetUserByIdUseCase getUserByIdUseCase,
                                      BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases,
                                      CurrentUserPort currentUserPort) {
        this.getUserPageUseCase = getUserPageUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.banUnbanDeleteUserUseCases = banUnbanDeleteUserUseCases;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping("/users")
    public String usersPage(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String q,
                            @RequestParam(required = false) String status,
                            Model model) {
        Boolean banned = null;
        if ("banned".equalsIgnoreCase(status)) banned = true;
        if ("active".equalsIgnoreCase(status)) banned = false;

        var userPage = getUserPageUseCase.execute(q, banned, PageRequest.of(page, CHUNK_SIZE));
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", !userPage.isLast());
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("status", status == null ? "all" : status);
        return "admin/users";
    }

    @GetMapping("/users/chunk")
    @ResponseBody
    public UserChunkResponse userChunk(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(required = false) String q,
                                                           @RequestParam(required = false) String status) {
        Boolean banned = null;
        if ("banned".equalsIgnoreCase(status)) banned = true;
        if ("active".equalsIgnoreCase(status)) banned = false;

        var userPage = getUserPageUseCase.execute(q, banned, PageRequest.of(page, CHUNK_SIZE));
        return new UserChunkResponse(userPage.getContent(), !userPage.isLast());
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id) {
        var current = currentUserPort.getCurrentUser();
        if (current.isPresent()) {
            var target = getUserByIdUseCase.execute(id);
            if (current.get().getUsername().equals(target.getUsername())) {
                return "redirect:/admin/users?error=selfban";
            }
        }
        banUnbanDeleteUserUseCases.ban(id);
        return "redirect:/admin/users?updated";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        banUnbanDeleteUserUseCases.unban(id);
        return "redirect:/admin/users?updated";
    }

    
}
