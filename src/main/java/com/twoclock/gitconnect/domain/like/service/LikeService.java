package com.twoclock.gitconnect.domain.like.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.like.entity.Likes;
import com.twoclock.gitconnect.domain.like.repository.LikeRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addLikeToBoard(Long boardId, String githubId) {
        Board board = validateBoard(boardId);
        Member member = validateMember(githubId);
        boolean duplicated = likeRepository.existsByBoardAndMember(board, member);
        if (duplicated) {
            throw new CustomException(ErrorCode.DUPLICATED_LIKE);
        }

        Likes likes = Likes.builder()
                .board(board)
                .member(member)
                .build();
        likeRepository.save(likes);
    }

    @Transactional
    public void deleteLikeToBoard(Long boardId, String githubId) {
        Board board = validateBoard(boardId);
        Member member = validateMember(githubId);
        Likes likes = likeRepository.findByBoardAndMember(board, member).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_LIKE)
        );
        likeRepository.delete(likes);
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
}
