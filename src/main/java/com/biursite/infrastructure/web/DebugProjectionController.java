package com.biursite.infrastructure.web;

import com.biursite.infrastructure.projection.PostReadModelEntity;
import com.biursite.infrastructure.projection.PostReadModelRepository;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
@RequestMapping("/debug/projections")
public class DebugProjectionController {
    private final PostReadModelRepository postReadModelRepository;

    public DebugProjectionController(PostReadModelRepository postReadModelRepository) {
        this.postReadModelRepository = postReadModelRepository;
    }

    @GetMapping("/posts")
    public List<PostReadModelEntity> listPostReadModels() {
        return postReadModelRepository.findAll();
    }
}
