package com.twoclock.gitconnect.global.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class JwtRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String gitHubId, String refreshToken, long expiration) {
        redisTemplate.opsForValue().set(gitHubId, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String gitHubId) {
        return redisTemplate.opsForValue().get(gitHubId);
    }

    public void deleteRefreshToken(String gitHubId) {
        redisTemplate.delete(gitHubId);
    }

    public void addToBlacklist(String jwtToken, long expiration) {
        redisTemplate.opsForValue().set(jwtToken, "blacklisted", expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String jwtToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(jwtToken));
    }
}
