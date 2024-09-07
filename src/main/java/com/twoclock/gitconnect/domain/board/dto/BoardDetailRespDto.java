package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;

public record BoardDetailRespDto(
        Long id,
        String title,
        String content,
        String nickname,
        String category,
        MemberLoginRespDto member
) {
    public BoardDetailRespDto(Board board) {
        this(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getMember().getName(),
                board.getCategory().name(),
                MemberLoginRespDto.of(board.getMember())
        );
    }
}
