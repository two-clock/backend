package com.twoclock.gitconnect.openapi.github.dto;

public record MemberGithubInfoDto(
        String login,
        String gitHubId,
        String avatarUrl,
        String name,
        String bio,
        String company,
        String location,
        String htmlUrl,
        String email,
        String blog,
        int followers,
        int following,
        int publicRepos,
        int publicGists,
        String createdAt,
        String updatedAt
) {
}
