package com.twoclock.gitconnect.domain.member.dto;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
import com.twoclock.gitconnect.openapi.github.dto.RepositoryRespDto;

import java.util.List;

public record MemberProfileRespDto(
        String login,
        String gitHubId,
        String avatarUrl,
        String name,
        List<FollowRespDto> followers,
        List<FollowRespDto> followings,
        List<RepositoryRespDto> repositories
) {
    public MemberProfileRespDto(
            Member member,
            List<FollowRespDto> followers,
            List<FollowRespDto> followings,
            List<RepositoryRespDto> repositories
    ) {
        this(
                member.getLogin(),
                member.getGitHubId(),
                member.getAvatarUrl(),
                member.getName(),
                followers,
                followings,
                repositories
        );
    }
}
