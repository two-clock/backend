package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.comment.dto.CommentListRespDto;
import com.twoclock.gitconnect.domain.like.dto.LikesRespDto;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;

import java.util.List;

public record BoardDetailRespDto(
        Long id,
        String title,
        String content,
        String nickname,
        String category,
        MemberLoginRespDto member,
        Long commentCount,
        List<CommentListRespDto> commentList,
        Long likeCount,
        List<LikesRespDto> likesList
) {
}
