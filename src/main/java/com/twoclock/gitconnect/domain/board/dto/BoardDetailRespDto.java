package com.twoclock.gitconnect.domain.board.dto;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.comment.dto.CommentListRespDto;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import com.twoclock.gitconnect.domain.like.dto.LikesRespDto;
import com.twoclock.gitconnect.domain.like.entity.Likes;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.twoclock.gitconnect.domain.comment.dto.CommentListRespDto.commentListResult;
import static com.twoclock.gitconnect.domain.like.dto.LikesRespDto.likesListResult;

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
    public BoardDetailRespDto(Board board, Page<Comment> comment, Page<Likes> likes) {
        this(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getMember().getName(),
                board.getCategory().name(),
                MemberLoginRespDto.of(board.getMember()),
                comment.getTotalElements(),
                commentListResult(comment.getContent()),
                likes.getTotalElements(),
                likesListResult(likes.getContent())
        );
    }
}
