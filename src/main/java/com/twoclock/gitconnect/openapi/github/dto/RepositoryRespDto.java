package com.twoclock.gitconnect.openapi.github.dto;

public record RepositoryRespDto(
        String name,
        String fullName,
        String visibility,
        String htmlUrl,
        String description,
        String createdAt
) {
}
