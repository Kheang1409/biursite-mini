package com.biursite.application.post.usecase;

import com.biursite.application.post.dto.PostView;
import com.biursite.application.shared.pagination.Page;
import com.biursite.application.shared.pagination.PageRequest;

public interface ListPostsUseCase {
    Page<PostView> execute(PageRequest pageRequest);
}
