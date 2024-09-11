package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.board.entity.BoardFile;

public record BoardFileRespDto(
        Long id,
        String originalName,
        String fileUrl
) {
    public BoardFileRespDto(BoardFile boardFile) {
        this(
                boardFile.getId(),
                boardFile.getOriginalName(),
                boardFile.getFileUrl()
        );
    }
}
