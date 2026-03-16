package com.biursite.application.shared.pagination;

public final class PageRequest {
    private final int page;
    private final int size;

    private PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.max(1, size);
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
}