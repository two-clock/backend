package com.twoclock.gitconnect.global.jwt.dto;

public record JwtTokenInfoDto(
        String accessToken,
        String refreshToken
) {
}
