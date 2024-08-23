package com.twoclock.gitconnect.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomRespDto(
        String login,
        String avatarUrl,
        String lastMessage,
        LocalDateTime createdDateTime
) {
}
