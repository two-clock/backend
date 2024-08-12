package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.member.entity.Member;
import lombok.Builder;

public class BoardResponseDto {

    @Builder
    public record BoardRespDto(
            Long id,
            String title,
            String content,
            String nickname,
            String category,
            Member member
    ) {
        public BoardRespDto(Board board) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getNickname(),
                    board.getCategory().name(),
                    board.getMember()
            );
        }

    }

}
