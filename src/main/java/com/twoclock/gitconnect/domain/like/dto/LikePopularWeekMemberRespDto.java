package com.twoclock.gitconnect.domain.like.dto;

import com.querydsl.core.annotations.QueryProjection;

public record LikePopularWeekMemberRespDto(String login, String avatarUrl) {

    @QueryProjection
    public LikePopularWeekMemberRespDto {
    }
}
