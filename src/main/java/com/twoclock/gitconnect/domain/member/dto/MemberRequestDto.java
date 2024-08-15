package com.twoclock.gitconnect.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public class MemberRequestDto {

    public record MemberModifyReqDto(
            @NotBlank(message = "아이디를 입력해주시길 바랍니다.")
            String login,

            @NotBlank(message = "이름을 입력해주시길 바랍니다.")
            String name
    ) {
    }
}
