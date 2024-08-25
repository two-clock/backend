package com.twoclock.gitconnect.domain.comment.dto;

import com.twoclock.gitconnect.domain.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentListRespDto(
        Long id,
        Long memberId,
        String avatarUrl,
        String nickname,
        String content,
        LocalDateTime modifiedDateTime,
        LocalDateTime createdDateTime
) {
    public CommentListRespDto(Comment comment) {
        this(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getAvatarUrl(),
                comment.getNickname(),
                comment.getContent(),
                comment.getModifiedDateTime(),
                comment.getCreatedDateTime()
        );
    }
}
