package com.twoclock.gitconnect.domain.comment.dto;

public record CommentListRespDto(
        Long id,
        Long memberId,
        String avatarUrl,
        String nickname,
        String content,
        String createdAt
) {
}
