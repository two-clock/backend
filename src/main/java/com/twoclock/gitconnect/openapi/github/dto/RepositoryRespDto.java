package com.twoclock.gitconnect.openapi.github.dto;

public record RepositoryRespDto(
        String name,
        String fullName,
        String htmlUrl,
        String description,
        String createdAt,
        String updatedAt,
        String pushedAt,
        String size,
        String stargazersCount,
        String watchersCount,
        String language,
        String forksCount
) {
}
