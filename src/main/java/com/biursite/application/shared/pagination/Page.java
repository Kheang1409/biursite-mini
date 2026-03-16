package com.biursite.application.shared.pagination;

import java.util.List;

public interface Page<T> {
    List<T> getContent();
    int getPageNumber();
    int getPageSize();
    long getTotalElements();
    int getTotalPages();
    boolean isFirst();
    boolean isLast();
}