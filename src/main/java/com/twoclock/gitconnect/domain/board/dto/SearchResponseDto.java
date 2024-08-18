package com.twoclock.gitconnect.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import lombok.Builder;

@Builder
public record SearchResponseDto(
        Long id,
        String title,
        String content,
        String nickname,
        String category,
        MemberLoginRespDto member
) {

    @QueryProjection
    public SearchResponseDto(Long id, String title, String content, String nickname, String category, MemberLoginRespDto member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.category = category;
        this.member = member;
    }
}


