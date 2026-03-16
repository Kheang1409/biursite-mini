package com.biursite.application.shared.pagination;

import java.util.List;

public class PageImpl<T> implements Page<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;

    public PageImpl(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public List<T> getContent() { return content; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return (int) Math.ceil((double) totalElements / pageSize); }
    public boolean isFirst() { return pageNumber == 0; }
    public boolean isLast() { return pageNumber >= getTotalPages() - 1; }
}
