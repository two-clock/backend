package com.twoclock.gitconnect.domain.like.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.like.entity.Likes;

import java.time.LocalDateTime;

public record LikesRespDto(
        Long id,
        String login,
        BoardRespDto board,
        String avatarUrl,
        String name,
        String gitHubId,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdDateTime
) {
    public LikesRespDto(Likes likes) {
        this(
                likes.getId(),
                likes.getMember().getLogin(),
                likes.getBoard() != null ? new BoardRespDto(likes.getBoard()) : null,
                likes.getMember().getAvatarUrl(),
                likes.getMember().getName(),
                likes.getMember().getGitHubId(),
                likes.getCreatedDateTime()
        );
    }
}
