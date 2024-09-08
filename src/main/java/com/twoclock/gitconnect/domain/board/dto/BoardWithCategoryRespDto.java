package com.twoclock.gitconnect.domain.board.dto;

import java.time.LocalDateTime;

public record BoardWithCategoryRespDto(
        Long id,
        String title,
        String content,
        String login,
        String thumbnailUrl,
        LocalDateTime createdDateTime
) {
}
