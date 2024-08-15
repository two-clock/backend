package com.twoclock.gitconnect.domain.member.dto;

public class MemberResponseDto {

    public record MemberModifyRespDto(
            String login,
            String avatarUrl,
            String name
    ) {
    }
}
