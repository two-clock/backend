package com.twoclock.gitconnect.domain.board.web;

import com.twoclock.gitconnect.domain.board.dto.BoardReqeustDto;
import com.twoclock.gitconnect.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.twoclock.gitconnect.domain.board.dto.BoardReqeustDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
@RestController
public class BoardController {

    private final BoardService boardService;

    @PostMapping("")
    public ResponseEntity<?> saveBoard(@RequestBody @Valid BoardSaveReqDto boardSaveReqDto,
                                       BindingResult bindingResult) throws BindException {
        // TODO : 로그인 유저인지 검증 필요
        Long userId = 100L;

        // TODO : BindException 핸들링 필요

        return  boardService.saveBoard(boardSaveReqDto, userId);
        // TODO : 공통 ResponseDTO(코드, 상태값, 객체) 생성 필요
    }
}
