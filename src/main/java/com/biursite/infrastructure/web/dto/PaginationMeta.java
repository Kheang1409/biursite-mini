package com.biursite.infrastructure.web.dto;

import com.biursite.application.shared.pagination.Page;

public record PaginationMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static PaginationMeta from(Page<?> page) {
        return new PaginationMeta(
                page.getPageNumber(),
                page.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                !page.isLast(),
                !page.isFirst()
        );
    }
}
