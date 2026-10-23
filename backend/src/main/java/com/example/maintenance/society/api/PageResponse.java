package com.example.maintenance.society.api;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int pageNumber, int pageSize, long totalElements,
    int totalPages, boolean isFirst, boolean isLast) {
    public static <S,T> PageResponse<T> from(Page<S> page, Function<S,T> mapper) {
        return new PageResponse<>(page.getContent().stream().map(mapper).toList(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
