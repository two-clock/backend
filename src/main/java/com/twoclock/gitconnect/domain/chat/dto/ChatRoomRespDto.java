package com.twoclock.gitconnect.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomRespDto(
        Long id,
        String chatRoomId,
        String login,
        String avatarUrl,
        String lastMessage,
        LocalDateTime createdDateTime
) {
}
