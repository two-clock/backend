package com.twoclock.gitconnect.domain.board.web;

import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto;
import com.twoclock.gitconnect.domain.board.service.BoardService;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.twoclock.gitconnect.domain.board.dto.BoardReqeustDto.*;
import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.*;
import static com.twoclock.gitconnect.global.exception.constants.ErrorCode.*;

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
        if(boardRespDto == null) throw new CustomException(FAIL_SAVE_BOARD);

        return RestResponse.OK();
//        return new RestResponse(boardRespDto);
    }
}
