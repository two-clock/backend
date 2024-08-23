package com.twoclock.gitconnect.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRegistReqDto(
        @NotNull(message = "게시판 키가 존재하지 않습니다.")
        Long boardId,
        @Size(max = 50, message = "댓글은 50자 이내로 작성해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}
