package com.twoclock.gitconnect.domain.board.service;

import com.amazonaws.util.IOUtils;
import com.twoclock.gitconnect.domain.board.dto.BoardCacheDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardModifyReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardSaveReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardCacheRepository;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.model.FileDto;
import com.twoclock.gitconnect.global.s3.S3Service;
import com.twoclock.gitconnect.global.util.FileUtil;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardCacheRepository boardCacheRepository;
    private final S3Service s3Service;

    @Transactional
    public BoardRespDto saveBoard(BoardSaveReqDto boardSaveReqDto, String githubId, MultipartFile[] files)  {
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

        // 파일 업로드 구현 구간
        if (files != null) {
            if(files.length > 3){
                throw new CustomException(ErrorCode.MANY_UPLOAD_IMAGES_BOARD);
            }
            for (MultipartFile file : files) {
                FileDto fileDto = FileUtil.convertFileToFileUploadDto(file);
                System.out.println(fileDto.toString());
                try {
                     String fileUrl = s3Service.uploadFile(fileDto.uuid(), file);
                    System.out.println("file: " +fileUrl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        boardCacheRepository.setBoardCache(key, new BoardCacheDto(boardPS));
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
    public Page<SearchResponseDto> getBoardList(SearchRequestDto searchRequestDto, String githubId) {
        searchRequestDto = checkGetReportBoards(searchRequestDto, githubId);
        PageRequest pageRequest = searchRequestDto.toPageRequest();
        return  boardRepository.searchBoardList(searchRequestDto, pageRequest);
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
        BoardCacheDto boardCacheDto = boardCacheRepository.getBoardCache(key);
        if (boardCacheDto != null){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime registerTime = LocalDateTime.parse(boardCacheDto.createdAt());
            if (now.minusMinutes(5).isBefore(registerTime)) {
                throw new CustomException(ErrorCode.MANY_SAVE_REQUEST_BOARD);
            }
        }
    }

    private SearchRequestDto checkGetReportBoards(SearchRequestDto searchRequestDto, String githubId) {
        if (Category.BD3.equals(Category.of(searchRequestDto.category()))) {
            if (githubId == null || githubId.isEmpty()) {
                throw new CustomException(ErrorCode.NOT_USING_REPORT_BOARD);
            }
            Member member = validateMember(githubId);
            String role = member.getRole().toString();
            return searchRequestDto.changeUseMember(githubId, role);
        }
        return searchRequestDto;
    }


    private void filteringBadWord(String content) {
        BadWordFiltering filtering = new BadWordFiltering();
        if (filtering.check(content)) {
            throw new CustomException(ErrorCode.BAD_WORD);
        }
    }
}
