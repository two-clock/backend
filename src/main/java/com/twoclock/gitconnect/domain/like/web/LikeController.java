package com.twoclock.gitconnect.domain.like.web;

import com.twoclock.gitconnect.domain.like.dto.LikesRespDto;
import com.twoclock.gitconnect.domain.like.service.LikeService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}")
    public RestResponse addLikeToBoard(@PathVariable("boardId") Long boardId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String githubId = userDetails.getUsername();
        likeService.addLikeToBoard(boardId, githubId);
        return RestResponse.OK();
    }

    @DeleteMapping("/{boardId}")
    public RestResponse deleteLikeToBoard(@PathVariable("boardId") Long boardId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String githubId = userDetails.getUsername();
        likeService.deleteLikeToBoard(boardId, githubId);
        return RestResponse.OK();
    }

    @GetMapping("/{boardId}")
    public RestResponse getLikesByBoardId(@PathVariable("boardId") Long boardId) {
        List<LikesRespDto> result = likeService.getLikesByBoardId(boardId);
        return new RestResponse(result);
    }
}
