package com.twoclock.gitconnect.domain.like.dto;

import com.twoclock.gitconnect.domain.like.entity.Likes;

import java.time.LocalDateTime;

public record LikesRespDto(
        Long id,
        String login,
        String avatarUrl,
        String name,
        String gitHubId,
        LocalDateTime createdDateTime
) {
    public LikesRespDto(Likes likes) {
        this(
                likes.getId(),
                likes.getMember().getLogin(),
                likes.getMember().getAvatarUrl(),
                likes.getMember().getName(),
                likes.getMember().getGitHubId(),
                likes.getCreatedDateTime()
        );
    }
}
