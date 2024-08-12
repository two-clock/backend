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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public RestResponse saveBoard(@RequestBody @Valid BoardSaveReqDto boardSaveReqDto){
        // TODO : 로그인 유저인지 검증 필요
        Long userId = 1L;

        BoardRespDto boardRespDto = boardService.saveBoard(boardSaveReqDto, userId);

        return RestResponse.OK();
//        return new RestResponse(boardRespDto);
    }

    @PutMapping
    public RestResponse updateBoard(@RequestBody @Valid BoardModifyReqDto boardUpdateReqDto) {
        // TODO : 로그인 유저인지 검증 필요
        Long userId = 1L;

        BoardRespDto boardRespDto = boardService.modifyBoard(boardUpdateReqDto, userId);

        return RestResponse.OK();
//        return new RestResponse(boardRespDto);
    }


    @GetMapping
    public RestResponse getBoardList(@ModelAttribute @Valid SearchRequestDto searchRequestDto) {

        Page<SearchResponseDto> boardRespDto = boardService.getBoardList(searchRequestDto);

        return new RestResponse(boardRespDto);

    }

    @DeleteMapping("/{boardKey}")
    public RestResponse deleteBoard(@PathVariable("boardKey") Long boardKey) {
        // TODO : 로그인 유저인지 검증 필요
        Long userId = 1L;

        boardService.deleteBoard(boardKey, userId);

        return RestResponse.OK();

    }

}
