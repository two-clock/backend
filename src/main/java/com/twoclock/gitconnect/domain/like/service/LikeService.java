package com.twoclock.gitconnect.domain.like.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.like.entity.Like;
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
    public void addLikeToBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        boolean duplicated = likeRepository.existsByBoardAndMember(board, member);
        if (duplicated) {
            throw new CustomException(ErrorCode.DUPLICATED_LIKE);
        }

        Like like = Like.builder()
                .board(board)
                .member(member)
                .build();
        likeRepository.save(like);
    }
}
