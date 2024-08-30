package com.twoclock.gitconnect.domain.board.web;

import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardModifyReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardRequestDto.BoardSaveReqDto;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.service.BoardService;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    public RestResponse updateBoard(@RequestBody @Valid BoardModifyReqDto boardUpdateReqDto,
                                    @PathVariable("boardId") Long boardId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        BoardRespDto boardRespDto = boardService.modifyBoard(boardUpdateReqDto, boardId, githubId);
        return RestResponse.OK();
    }

    @GetMapping
    public RestResponse getBoardList(@ModelAttribute @Valid SearchRequestDto searchRequestDto,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails == null ? null : userDetails.getUsername();
        Page<SearchResponseDto> boardRespDto = boardService.getBoardList(searchRequestDto, githubId);
        return new RestResponse(boardRespDto);
    }

    @DeleteMapping("/{boardId}")
    public RestResponse deleteBoard(@PathVariable("boardId") Long boardId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        boardService.deleteBoard(boardId, githubId);
        return RestResponse.OK();

    }

}
