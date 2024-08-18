package com.twoclock.gitconnect.openapi.github.dto;

public record GitHubTokenDto(
        String accessToken,
        String refreshToken
) {
}
