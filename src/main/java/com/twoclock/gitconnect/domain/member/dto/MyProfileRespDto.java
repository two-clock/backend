package com.twoclock.gitconnect.domain.member.dto;

import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.comment.dto.MyCommentRespDto;
import com.twoclock.gitconnect.domain.like.dto.LikesRespDto;
import com.twoclock.gitconnect.openapi.github.dto.MemberGithubInfoDto;
import com.twoclock.gitconnect.openapi.github.dto.RepositoryRespDto;

import java.util.List;

public record MyProfileRespDto(
        MemberGithubInfoDto info,
        List<RepositoryRespDto> repositories,
        List<BoardRespDto> boards,
        List<MyCommentRespDto> comments,
        List<LikesRespDto> likes
) {
    public MyProfileRespDto(
            MemberGithubInfoDto info,
            List<RepositoryRespDto> repositories,
            List<BoardRespDto> boards,
            List<MyCommentRespDto> comments,
            List<LikesRespDto> likes
    ) {
        this.info = info;
        this.repositories = repositories;
        this.boards = boards;
        this.comments = comments;
        this.likes = likes;
    }
}
