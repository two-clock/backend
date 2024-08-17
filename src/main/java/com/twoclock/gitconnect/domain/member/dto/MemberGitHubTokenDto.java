package com.twoclock.gitconnect.domain.member.dto;

public record MemberGitHubTokenDto(
        String accessToken,
        String refreshToken
) {
}
