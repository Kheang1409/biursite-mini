package com.biursite.controller.web;

import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.usecase.GetUserPageUseCase;
import com.biursite.application.user.usecase.GetUserByIdUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.application.user.mapper.UserMapper;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RolesAllowed({"ADMIN"})
public class AdminPageController {
    private static final int CHUNK_SIZE = 10;

    private final GetUserPageUseCase getUserPageUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases;

    public AdminPageController(GetUserPageUseCase getUserPageUseCase,
                               GetUserByIdUseCase getUserByIdUseCase,
                               BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases) {
        this.getUserPageUseCase = getUserPageUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.banUnbanDeleteUserUseCases = banUnbanDeleteUserUseCases;
    }

    @GetMapping("/users")
    public String usersPage(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String q,
                            @RequestParam(required = false) String status,
                            Model model) {
        Boolean banned = null;
        if ("banned".equalsIgnoreCase(status)) banned = true;
        if ("active".equalsIgnoreCase(status)) banned = false;

        var userPage = getUserPageUseCase.execute(q, banned, org.springframework.data.domain.PageRequest.of(page, CHUNK_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))).map(UserMapper::toDto);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", userPage.hasNext());
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

        var userPage = getUserPageUseCase.execute(q, banned, org.springframework.data.domain.PageRequest.of(page, CHUNK_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))).map(UserMapper::toDto);
        return new UserChunkResponse(userPage.getContent(), userPage.hasNext());
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id, Authentication auth) {
        if (auth != null && auth.getName() != null) {
            var target = UserMapper.toDto(getUserByIdUseCase.execute(id));
            if (auth.getName().equals(target.getUsername())) {
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

    public record UserChunkResponse(List<UserDto> users, boolean hasNext) {}
}
