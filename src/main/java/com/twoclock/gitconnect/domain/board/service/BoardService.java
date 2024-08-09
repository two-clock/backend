package com.twoclock.gitconnect.domain.board.service;

import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.*;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.*;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        Category code = Category.of(boardSaveReqDto.category());

        Board board = Board.builder()
                .title(boardSaveReqDto.title())
                .content(boardSaveReqDto.content())
                .nickname(member.getName())
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

    @Transactional(readOnly = true)
    public Page<SearchResponseDto> getBoardList(SearchRequestDto searchRequestDto) {

        // TODO: 카테고리 타입 확인

        // TODO: 신고 게시판 정책 추가

        //

        // 페이지 요청 객체 생성
        PageRequest pageRequest = searchRequestDto.toPageRequest();

        Page<SearchResponseDto> boardList = boardRepository.searchBoardList(searchRequestDto, pageRequest);

        return boardList;
    }
}
