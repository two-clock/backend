package com.twoclock.gitconnect.domain.board.web;

import com.twoclock.gitconnect.domain.board.dto.BoardDetailRespDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardModifyReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardSaveReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.board.dto.BoardWithCategoryRespDto;
import com.twoclock.gitconnect.domain.board.service.BoardService;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {

    private final BoardService boardService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public RestResponse saveBoard(@Valid @RequestPart BoardSaveReqDto boardSaveReqDto,
                                  @RequestPart(required = false) MultipartFile[] files,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        BoardRespDto boardRespDto = boardService.saveBoard(boardSaveReqDto, githubId, files);
        return RestResponse.OK();
    }

    @PutMapping("/{boardId}")
    public RestResponse updateBoard(@RequestPart @Valid BoardModifyReqDto boardUpdateReqDto,
                                    @PathVariable("boardId") Long boardId,
                                    @RequestPart(required = false) MultipartFile[] files,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        BoardRespDto boardRespDto = boardService.modifyBoard(boardUpdateReqDto, boardId, githubId, files);
        return RestResponse.OK();
    }

//     TODO: 이걸 Search 기능으로 바꾸기
//    @GetMapping
//    public RestResponse getBoardList(@ModelAttribute @Valid SearchRequestDto searchRequestDto,
//                                     @Nullable @AuthenticationPrincipal UserDetails userDetails) {
//        String githubId = userDetails == null ? null : userDetails.getUsername();
//        Page<SearchResponseDto> boardRespDto = boardService.getBoardList(searchRequestDto, githubId);
//        return new RestResponse(boardRespDto);
//    }

    @GetMapping("/{boardId}")
    public RestResponse getBoardDetail(@PathVariable("boardId") Long boardId,
                                       @Nullable @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails == null ? null : userDetails.getUsername();
        BoardDetailRespDto result = boardService.getBoardDetail(boardId, githubId);
        return new RestResponse(result);
    }

    @GetMapping
    public RestResponse getBoardListWithCategory(
            @RequestParam(value = "category") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        List<BoardWithCategoryRespDto> responseDtos = boardService.getBoardListWithCategory(category, page, size);
        return new RestResponse(responseDtos);
    }

    @DeleteMapping("/{boardId}")
    public RestResponse deleteBoard(@PathVariable("boardId") Long boardId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        boardService.deleteBoard(boardId, githubId);
        return RestResponse.OK();

    }

}
