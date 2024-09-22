package com.twoclock.gitconnect.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.twoclock.gitconnect.domain.member.entity.Member;

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

    public MemberLoginRespDto(Member member) {
        this(
                member.getLogin(),
                member.getGitHubId(),
                member.getAvatarUrl(),
                member.getName()
        );
    }
}
