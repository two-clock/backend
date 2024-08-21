package com.twoclock.gitconnect.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageSaveRespDto(
        String login,
        String gitHubId,
        String message,
        LocalDateTime createdDateTime
) {
}
