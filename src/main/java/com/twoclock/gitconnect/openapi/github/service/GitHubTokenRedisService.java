package com.twoclock.gitconnect.openapi.github.service;

import com.twoclock.gitconnect.openapi.github.dto.GitHubTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GitHubTokenRedisService {

    private static final String GITHUB_ID = "github-id:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveGitHubToken(String gitHubId, String gitHubAccessToken, String gitHubRefreshToken) {
        String key = GITHUB_ID + gitHubId;
        GitHubTokenDto gitHubTokenDto = new GitHubTokenDto(gitHubAccessToken, gitHubRefreshToken);
        redisTemplate.opsForValue().set(key, gitHubTokenDto);
    }

    public GitHubTokenDto getGitHubToken(String gitHubId) {
        String key = GITHUB_ID + gitHubId;
        return (GitHubTokenDto) redisTemplate.opsForValue().get(key);
    }

    public void deleteGitHubToken(String gitHubId) {
        redisTemplate.delete(GITHUB_ID + gitHubId);
    }
}
