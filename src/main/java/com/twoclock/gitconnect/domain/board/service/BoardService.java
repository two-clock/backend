package com.twoclock.gitconnect.domain.board.service;

import com.twoclock.gitconnect.domain.board.dto.BoardCacheDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardModifyReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardSaveReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public BoardRespDto saveBoard(BoardSaveReqDto boardSaveReqDto, String githubId) {
        Category code = Category.of(boardSaveReqDto.category());
        String key = "board:" + githubId + ":" + code;

        validateManyRequestBoard(key);
        filteringBadWord(boardSaveReqDto.content());
        Member member = validateMember(githubId);

        Board board = Board.builder()
                .title(boardSaveReqDto.title())
                .content(boardSaveReqDto.content())
                .nickname(member.getName())
                .category(code)
                .member(member)
                .build();
        Board boardPS = boardRepository.save(board);
        redisTemplate.opsForValue().set(key, new BoardCacheDto(boardPS));
        return new BoardRespDto(boardPS);
    }

    @Transactional
    public BoardRespDto modifyBoard(BoardModifyReqDto boardUpdateReqDto, Long boardId, String githubId) {
        filteringBadWord(boardUpdateReqDto.content());
        Member member = validateMember(githubId);
        Board board = validateBoard(boardId);
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

    @Transactional
    public void deleteBoard(Long boardId, String githubId) {
        Member member = validateMember(githubId);
        Board board = validateBoard(boardId);
        board.checkUserId(member.getId());
        boardRepository.delete(board);

    }

    private Member validateMember(String githubId) {
        return memberRepository.findByGitHubId(githubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private Board validateBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );
    }

    private void validateManyRequestBoard(String key) {
        BoardCacheDto boardCacheDto = (BoardCacheDto) redisTemplate.opsForValue().get(key);
        if (boardCacheDto != null){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime registerTime = LocalDateTime.parse(boardCacheDto.createdAt());
            if (now.minusMinutes(5).isBefore(registerTime)) {
                throw new CustomException(ErrorCode.MANY_SAVE_REQUEST_BOARD);
            }
        }
    }

    private void filteringBadWord(String content) {
        BadWordFiltering filtering = new BadWordFiltering();
        if (filtering.check(content)) {
            throw new CustomException(ErrorCode.BAD_WORD);
        }
    }
}
