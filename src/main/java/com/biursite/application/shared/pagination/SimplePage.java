package com.biursite.application.shared.pagination;

import java.util.List;

public final class SimplePage<T> implements Page<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public SimplePage(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    @Override
    public List<T> getContent() { return content; }

    @Override
    public int getPageNumber() { return pageNumber; }

    @Override
    public int getPageSize() { return pageSize; }

    @Override
    public long getTotalElements() { return totalElements; }

    @Override
    public int getTotalPages() { return totalPages; }

    @Override
    public boolean isFirst() { return pageNumber == 0; }

    @Override
    public boolean isLast() { return pageNumber + 1 >= totalPages; }
}
