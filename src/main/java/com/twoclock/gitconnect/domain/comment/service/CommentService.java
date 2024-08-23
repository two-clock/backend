package com.twoclock.gitconnect.domain.comment.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.comment.dto.CommentRegistReqDto;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import com.twoclock.gitconnect.domain.comment.repository.CommentRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void saveComment(CommentRegistReqDto dto, String githubId) {
        filteringBadWord(dto.content());
        Board board = validateBoard(dto.boardId());
        Member member = validateMember(githubId);

        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .nickname(member.getName())
                .content(dto.content())
                .build();
        commentRepository.save(comment);
    }

    private Board validateBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );
    }

    private Member validateMember(String githubId) {
        return memberRepository.findByGitHubId(githubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private void filteringBadWord(String content) {
        BadWordFiltering filtering = new BadWordFiltering();
        if (filtering.check(content)) {
            throw new CustomException(ErrorCode.BAD_WORD);
        }
    }
}
