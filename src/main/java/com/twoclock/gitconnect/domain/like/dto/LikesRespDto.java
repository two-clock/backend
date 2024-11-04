package com.twoclock.gitconnect.domain.like.dto;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.like.entity.Likes;

import java.time.LocalDateTime;
import java.util.List;

public record LikesRespDto(
        Long id,
        String login,
        Board board,
        String avatarUrl,
        String name,
        String gitHubId,
        LocalDateTime createdDateTime
) {
    public LikesRespDto(Likes likes) {
        this(
                likes.getId(),
                likes.getMember().getLogin(),
                likes.getBoard(),
                likes.getMember().getAvatarUrl(),
                likes.getMember().getName(),
                likes.getMember().getGitHubId(),
                likes.getCreatedDateTime()
        );
    }
}
