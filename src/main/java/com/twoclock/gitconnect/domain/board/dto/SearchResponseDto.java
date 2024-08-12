package com.twoclock.gitconnect.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.twoclock.gitconnect.domain.member.dto.MemberInfoDto;
import lombok.Builder;

@Builder
public record SearchResponseDto(
        Long id,
        String title,
        String content,
        String nickname,
        String category,
        MemberInfoDto member
) {

    @QueryProjection
    public SearchResponseDto(Long id, String title, String content, String nickname, String category, MemberInfoDto member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.category = category;
        this.member = member;
    }
}


