package com.twoclock.gitconnect.domain.like.web;

import com.twoclock.gitconnect.domain.like.service.LikeService;
import com.twoclock.gitconnect.global.model.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}")
    public RestResponse addLikeToBoard(@PathVariable("boardId") Long boardId) {
        Long userId = 1L;
        likeService.addLikeToBoard(boardId, userId);
        return RestResponse.OK();
    }

    @DeleteMapping("/{boardId}")
    public RestResponse deleteLikeToBoard(@PathVariable("boardId") Long boardId) {
        Long userId = 1L;
        likeService.deleteLikeToBoard(boardId, userId);
        return RestResponse.OK();
    }



}
