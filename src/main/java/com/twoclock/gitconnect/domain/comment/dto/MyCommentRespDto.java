package com.twoclock.gitconnect.domain.comment.dto;

import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.*;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.comment.entity.Comment;

import java.time.LocalDateTime;

public record MyCommentRespDto(
        Long id,
        Long memberId,
        BoardRespDto board,
        String githubId,
        String nickname,
        String content,
        LocalDateTime modifiedDateTime,
        LocalDateTime createdDateTime
) {
    public MyCommentRespDto(Comment comment, Board board) {
        this(
                comment.getId(),
                comment.getMember().getId(),
                new BoardRespDto(board),
                comment.getMember().getLogin(),
                comment.getNickname(),
                comment.getContent(),
                comment.getModifiedDateTime(),
                comment.getCreatedDateTime()
        );
    }
}
