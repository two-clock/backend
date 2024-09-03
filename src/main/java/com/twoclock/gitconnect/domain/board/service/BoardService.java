package com.twoclock.gitconnect.domain.board.service;

import com.twoclock.gitconnect.domain.board.dto.BoardCacheDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardModifyReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardSaveReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.BoardFile;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardCacheRepository;
import com.twoclock.gitconnect.domain.board.repository.BoardFileRepository;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.s3.S3Service;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardCacheRepository boardCacheRepository;
    private final BoardFileRepository boardFileRepository;
    private final S3Service s3Service;

    private static final int MAX_BOARD_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final List<String> PERMIT_BOARD_IMAGE_TYPE = List.of("image/jpeg", "image/jpg", "image/png");

    @Transactional
    public BoardRespDto saveBoard(BoardSaveReqDto boardSaveReqDto, String githubId, MultipartFile[] files) {
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

        if (files.length != 0) boardImageUpload(files, boardPS);
        boardCacheRepository.setBoardCache(key, new BoardCacheDto(boardPS));
        return new BoardRespDto(boardPS);
    }

    @Transactional
    public BoardRespDto modifyBoard(BoardModifyReqDto boardUpdateReqDto, Long boardId, String githubId, MultipartFile[] files) {
        filteringBadWord(boardUpdateReqDto.content());
        Member member = validateMember(githubId);
        Board board = validateBoard(boardId);
        board.checkUserId(member.getId());

        deleteBoardImage(boardId, boardUpdateReqDto.fileOriginImageList());

        if (files.length != 0) boardImageUpload(files, board);
        board.updateBoard(boardUpdateReqDto.title(), boardUpdateReqDto.content());

        return new BoardRespDto(board);
    }

    @Transactional(readOnly = true)
    public Page<SearchResponseDto> getBoardList(SearchRequestDto searchRequestDto, String githubId) {
        searchRequestDto = checkGetReportBoards(searchRequestDto, githubId);
        PageRequest pageRequest = searchRequestDto.toPageRequest();
        return boardRepository.searchBoardList(searchRequestDto, pageRequest);
    }

    @Transactional
    public void deleteBoard(Long boardId, String githubId) {
        Member member = validateMember(githubId);
        Board board = validateBoard(boardId);
        board.checkUserId(member.getId());
        deleteBoardImage(boardId, new ArrayList<>());
        boardRepository.delete(board);
    }

    private Member validateMember(String githubId) {
        return memberRepository.findByGitHubId(githubId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private Board validateBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
    }

    private void validateManyRequestBoard(String key) {
        BoardCacheDto boardCacheDto = boardCacheRepository.getBoardCache(key);
        if (boardCacheDto != null) {
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

    private void boardImageUpload(MultipartFile[] files, Board board) {
        if (files.length > 3) {
            throw new CustomException(ErrorCode.MANY_UPLOAD_IMAGES_BOARD);
        }
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String fileUrl = uploadFileUrl(file);
            BoardFile boardFile = BoardFile.builder().board(board).originalName(originalName).fileUrl(fileUrl).build();
            boardFileRepository.save(boardFile);
        }
    }

    private String uploadFileUrl(MultipartFile file) {
        if (file.getSize() > MAX_BOARD_IMAGE_SIZE) {
            throw new CustomException(ErrorCode.OVER_IMAGE_SIZE_BOARD);
        }

        if (!PERMIT_BOARD_IMAGE_TYPE.contains(file.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_TYPE_BOARD);
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String key = uuid + "." + fileExtension;
        try {
            return s3Service.uploadFile(key, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteBoardImage(Long boardId, List<String> boardOriginImageList) {
        List<BoardFile> boardFiles = boardFileRepository.findByBoardId(boardId);
        if (!boardFiles.isEmpty()) {
            if (boardOriginImageList != null && !boardOriginImageList.isEmpty()) {
                boardFiles = boardFiles.stream()
                        .filter(boardFile -> !boardOriginImageList.contains(boardFile.getFileUrl()))
                        .toList();
            }
            boardFiles.forEach(boardFile -> {
                boardFileRepository.delete(boardFile);
                s3Service.deleteFile(boardFile.getFileUrl());
            });
        }
    }

    private void filteringBadWord(String content) {
        BadWordFiltering filtering = new BadWordFiltering();
        if (filtering.check(content)) {
            throw new CustomException(ErrorCode.BAD_WORD);
        }
    }
}
