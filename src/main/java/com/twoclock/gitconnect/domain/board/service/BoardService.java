package com.twoclock.gitconnect.domain.board.service;

import com.twoclock.gitconnect.domain.board.dto.BoardReqeustDto.*;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.*;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public BoardRespDto saveBoard(BoardSaveReqDto boardSaveReqDto, Long userId) {

        Member member = memberRepository.findById(userId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        Category code = Category.of(boardSaveReqDto.category());

        Board board = Board.builder()
                .title(boardSaveReqDto.title())
                .content(boardSaveReqDto.content())
                .nickname(member.getNickname())
                .category(code)
                .member(member)
                .build();
        Board boardPS = boardRepository.save(board);
        return new BoardRespDto(boardPS);
    }

    @Transactional
    public BoardRespDto modifyBoard(BoardModifyReqDto boardUpdateReqDto, Long userId) {

        Member member = memberRepository.findById(userId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        Board board = boardRepository.findById(boardUpdateReqDto.id()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );
        board.checkUserId(member.getId());
        board.updateBoard(boardUpdateReqDto.title(), boardUpdateReqDto.content());

        return new BoardRespDto(board);
    }
}
