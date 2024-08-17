package com.twoclock.gitconnect.openapi.github.dto;

public record FollowRespDto(
        String login,
        String avatarUrl,
        String htmlUrl
) {
}
