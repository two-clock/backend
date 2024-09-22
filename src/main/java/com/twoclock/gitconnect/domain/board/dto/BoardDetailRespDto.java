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
        String category,
        long viewCount,
        long likeCount,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdDateTime,
        MemberLoginRespDto member,
        List<BoardFileRespDto> fileList
) {

    public BoardDetailRespDto(Board board) {
        this(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getCategory().name(),
                board.getViewCount(),
                board.getLikeList().size(),
                board.getCreatedDateTime(),
                new MemberLoginRespDto(board.getMember()),
                board.getFileList().stream().map(BoardFileRespDto::new).toList()
        );
    }
}
