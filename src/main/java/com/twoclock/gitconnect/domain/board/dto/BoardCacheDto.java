package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.board.entity.Board;

public record BoardCacheDto (
        Long id,
        String title,
        String content,
        String category,
        String gitHubId,
        String createdAt
){
    public BoardCacheDto(Board board) {
        this(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getCategory().name(),
                board.getMember().getGitHubId(),
                board.getCreatedDateTime().toString()
        );
    }
}
