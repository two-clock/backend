package com.twoclock.gitconnect.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;

public record MemberInfoDto(
        String login,
        String avatarUrl,
        String name
) {

    @QueryProjection
    public MemberInfoDto(String login, String avatarUrl, String name) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.name = name;
    }

}
