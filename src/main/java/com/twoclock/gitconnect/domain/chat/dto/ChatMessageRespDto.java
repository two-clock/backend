package com.twoclock.gitconnect.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageRespDto(
        String login,
        String message,
        LocalDateTime createdDateTime
) {
}
