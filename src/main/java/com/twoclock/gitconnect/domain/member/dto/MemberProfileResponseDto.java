package com.twoclock.gitconnect.domain.member.dto;

import com.twoclock.gitconnect.domain.member.entity.Member;

public record MemberProfileResponseDto(
        String login,
        String gitHubId,
        String avatarUrl,
        String name
) {
    public MemberProfileResponseDto(Member member) {
        this(member.getLogin(), member.getGitHubId(), member.getAvatarUrl(), member.getName());
    }
}
