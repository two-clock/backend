package com.twoclock.gitconnect.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRegistReqDto(
        @Size(max = 50, message = "댓글은 50자 이내로 작성해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}
