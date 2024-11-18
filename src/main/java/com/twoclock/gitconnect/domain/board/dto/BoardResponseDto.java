package com.twoclock.gitconnect.domain.board.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import lombok.Builder;

import java.time.LocalDateTime;

public class BoardResponseDto {

    @Builder
    public record BoardRespDto(
            Long id,
            String title,
            String content,
            String nickname,
            String category,
            MemberLoginRespDto member,
            Long viewsCount,
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            LocalDateTime createdDateTime
    ) {
        public BoardRespDto(Board board) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getNickname(),
                    board.getCategory().name(),
                    new MemberLoginRespDto(board.getMember()),
                    board.getViewCount(),
                    board.getCreatedDateTime()
            );
        }

    }

}
