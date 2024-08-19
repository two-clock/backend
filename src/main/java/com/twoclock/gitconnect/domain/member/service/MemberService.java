package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.member.dto.MemberProfileRespDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
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

    @Cacheable(value = "getMember", key = "'member:github-id:' + #gitHubId", cacheManager = "memberCacheManager")
    @Transactional(readOnly = true)
    public MemberProfileRespDto getMember(String gitHubId) {
        Member member = validateMember(gitHubId);
        String githubAccessToken = gitHubTokenRedisService.getGitHubToken(gitHubId).accessToken();

        List<FollowRespDto> followings = githubAPIService.getFollowing(githubAccessToken);
        List<FollowRespDto> followers = githubAPIService.getFollowers(githubAccessToken);
        List<RepositoryRespDto> repositories = githubAPIService.getRepositories(githubAccessToken);

        return new MemberProfileRespDto(member, followers, followings, repositories);
    }

    private Member validateMember(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }
}
