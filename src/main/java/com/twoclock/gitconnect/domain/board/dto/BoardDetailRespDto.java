package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;

import java.time.LocalDateTime;
import java.util.List;

public record BoardDetailRespDto(
        Long id,
        String title,
        String content,
        String nickname,
        String category,
        String createdDateTime,
        MemberLoginRespDto member,
        List<BoardFileRespDto> fileList
) {
    public BoardDetailRespDto(Board board, List<BoardFileRespDto> fileList) {
        this(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getMember().getName(),
                board.getCategory().name(),
                String.valueOf(board.getCreatedDateTime()),
                MemberLoginRespDto.of(board.getMember()),
                fileList
        );
    }
}
