package com.twoclock.gitconnect.domain.board.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
        Long viewCount,
        Long likeCount,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdDateTime,
        MemberLoginRespDto member,
        List<BoardFileRespDto> fileList
) {
    public BoardDetailRespDto(Board board, Long likeCount, List<BoardFileRespDto> fileList) {
        this(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getMember().getName(),
                board.getCategory().name(),
                board.getViewCount(),
                likeCount,
                board.getCreatedDateTime(),
                MemberLoginRespDto.of(board.getMember()),
                fileList
        );
    }
}
