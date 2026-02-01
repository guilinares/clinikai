package com.guilinares.clinikai.infrastructure.pagination;

public record PageMeta(
        int number,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {}