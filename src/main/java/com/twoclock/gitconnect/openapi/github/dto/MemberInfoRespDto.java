package com.twoclock.gitconnect.openapi.github.dto;

import java.util.List;

public record MemberInfoRespDto(
        MemberGithubInfoDto githubInfo,
        List<RepositoryRespDto> repositories
) {

    public MemberInfoRespDto(MemberGithubInfoDto githubInfo, List<RepositoryRespDto> repositories) {
        this.githubInfo = githubInfo;
        this.repositories = repositories;
    }
}
