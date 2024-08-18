package com.twoclock.gitconnect.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;

public record MemberLoginRespDto(
        String login,
        String gitHubId,
        String avatarUrl,
        String name
) {

    @QueryProjection
    public MemberLoginRespDto(String login, String gitHubId, String avatarUrl, String name) {
        this.login = login;
        this.gitHubId = gitHubId;
        this.avatarUrl = avatarUrl;
        this.name = name;
    }

}
