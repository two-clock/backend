package com.twoclock.gitconnect.domain.board.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.model.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.twoclock.gitconnect.domain.board.dto.BoardReqeustDto.*;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.*;
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
}
