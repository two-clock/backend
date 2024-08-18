package com.twoclock.gitconnect.global.jwt.dto;

public record JwtTokenDto(
        String accessToken,
        String refreshToken
) {
}
