package com.twoclock.gitconnect.domain.member.dto;

public class MemberRequestDto {

    public record MemberModifyReqDto(
            String login,
            String name
    ) {
    }
}
