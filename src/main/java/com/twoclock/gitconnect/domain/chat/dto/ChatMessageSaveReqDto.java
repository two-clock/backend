package com.twoclock.gitconnect.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageSaveReqDto(
        @NotBlank(message = "내용을 입력해주세요.")
        String message
) {
}
