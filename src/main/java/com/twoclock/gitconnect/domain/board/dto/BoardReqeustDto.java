package com.twoclock.gitconnect.domain.board.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardReqeustDto {

    public record BoardSaveReqDto(
            @NotBlank(message = "제목을 입력해주세요.")
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content,
            @NotBlank(message = "카테고리는 필수값 입니다.")
            String category
    ) {

    }
}
