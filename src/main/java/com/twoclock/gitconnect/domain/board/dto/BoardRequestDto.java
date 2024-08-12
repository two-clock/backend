package com.twoclock.gitconnect.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BoardRequestDto {

    public record BoardSaveReqDto(
            @Size(max = 50, message = "제목은 50자 이내로 작성해주세요.")
            @NotBlank(message = "제목을 입력해주세요.")
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content,
            @NotBlank(message = "카테고리는 필수값 입니다.")
            String category
    ) {

    }

    public record BoardModifyReqDto(
            @NotNull(message = "게시판 키가 존재하지 않습니다.")
            Long id,
            @Size(max = 50, message = "제목은 50자 이내로 작성해주세요.")
            @NotBlank(message = "제목을 입력해주세요.")
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content

    ) {

    }

}
