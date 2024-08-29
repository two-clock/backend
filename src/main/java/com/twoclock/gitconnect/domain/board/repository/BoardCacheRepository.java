package com.twoclock.gitconnect.domain.board.repository;

import com.twoclock.gitconnect.domain.board.dto.BoardCacheDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class BoardCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public BoardCacheRepository(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void setBoardCache(String key, BoardCacheDto boardCacheDto) {
        redisTemplate.opsForValue().set(key, boardCacheDto, Duration.ofMinutes(5));
    }
    public BoardCacheDto getBoardCache(String key) {
        return (BoardCacheDto) redisTemplate.opsForValue().get(key);
    }
}
