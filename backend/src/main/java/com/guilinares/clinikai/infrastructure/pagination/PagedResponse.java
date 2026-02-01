package com.guilinares.clinikai.infrastructure.pagination;

public record PagedResponse<T>(
        java.util.List<T> items,
        PageMeta page
) {
    public static <T> PagedResponse<T> from(org.springframework.data.domain.Page<?> p, java.util.List<T> items) {
        return new PagedResponse<>(
                items,
                new PageMeta(
                        p.getNumber(),
                        p.getSize(),
                        p.getTotalElements(),
                        p.getTotalPages(),
                        p.hasNext(),
                        p.hasPrevious()
                )
        );
    }
}
