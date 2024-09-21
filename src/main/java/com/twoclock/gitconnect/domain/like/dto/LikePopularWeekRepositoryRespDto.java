package com.twoclock.gitconnect.domain.like.dto;

import com.querydsl.core.annotations.QueryProjection;

public record LikePopularWeekRepositoryRespDto(
        Long boardId,
        String title,
        String content,
        String login,
        Long likeCount
) {

    @QueryProjection
    public LikePopularWeekRepositoryRespDto {
    }
}
