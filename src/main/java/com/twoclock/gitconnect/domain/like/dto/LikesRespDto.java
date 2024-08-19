package com.twoclock.gitconnect.domain.like.dto;

import java.time.LocalDateTime;

public record LikesRespDto(
        Long id,
        String login,
        String avatarUrl,
        String name,
        String gitHubId,
        LocalDateTime createdDateTime
) {
}
