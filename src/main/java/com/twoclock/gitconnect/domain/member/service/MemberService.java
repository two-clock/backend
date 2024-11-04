package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.member.dto.MemberProfileRespDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
import com.twoclock.gitconnect.openapi.github.dto.MemberGithubInfoDto;
import com.twoclock.gitconnect.openapi.github.dto.MemberInfoRespDto;
import com.twoclock.gitconnect.openapi.github.dto.RepositoryRespDto;
import com.twoclock.gitconnect.openapi.github.service.GitHubTokenRedisService;
import com.twoclock.gitconnect.openapi.github.service.GithubAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GitHubTokenRedisService gitHubTokenRedisService;
    private final GithubAPIService githubAPIService;

    @Cacheable(value = "getMember", key = "'member:github-id:' + #gitHubId", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public MemberProfileRespDto getMember(String gitHubId) {
        Member member = validateMember(gitHubId);
        String name = member.getLogin();
        String githubAccessToken = gitHubTokenRedisService.getGitHubToken(gitHubId).accessToken();

        List<FollowRespDto> followings = githubAPIService.getFollowing(githubAccessToken);
        List<FollowRespDto> followers = githubAPIService.getFollowers(githubAccessToken);
        List<RepositoryRespDto> repositories = githubAPIService.getRepositories(githubAccessToken, name);

        return new MemberProfileRespDto(member, followers, followings, repositories);
    }

    @Transactional(readOnly = true)
    public MemberInfoRespDto getMemberInfo(String gitHubId, String userGitHubId) {
        Member userInfo = validateUserLoginName(userGitHubId);

        String githubAccessToken = gitHubTokenRedisService.getGitHubToken(gitHubId).accessToken();
        MemberGithubInfoDto info = githubAPIService.getGitHubMemberInfo(githubAccessToken, userGitHubId);

        List<RepositoryRespDto> repositories = githubAPIService.getRepositories(githubAccessToken, userGitHubId);

        return new MemberInfoRespDto(info, repositories);
    }

    private Member validateMember(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private Member validateUserLoginName(String login) {
        return memberRepository.findByLogin(login).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }
}
