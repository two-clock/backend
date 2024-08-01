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



        return ResponseEntity.ok().build();
    }
}
