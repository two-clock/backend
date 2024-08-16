package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.member.dto.MemberGitHubTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberAuthRedisService {

    private static final String GITHUB_ID = "github-id:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveGitHubToken(String gitHubId, String gitHubAccessToken, String gitHubRefreshToken) {
        String key = GITHUB_ID + gitHubId;
        MemberGitHubTokenDto memberGitHubTokenDto = new MemberGitHubTokenDto(gitHubAccessToken, gitHubRefreshToken);
        redisTemplate.opsForValue().set(key, memberGitHubTokenDto);
    }

    public MemberGitHubTokenDto getGitHubToken(String gitHubId) {
        String key = GITHUB_ID + gitHubId;
        return (MemberGitHubTokenDto) redisTemplate.opsForValue().get(key);
    }

    public void deleteGitHubToken(String gitHubId) {
        redisTemplate.delete(GITHUB_ID + gitHubId);
    }
}
