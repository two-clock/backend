package com.twoclock.gitconnect.domain.like.web;

import com.twoclock.gitconnect.domain.like.service.LikeService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public RestResponse getLikeByBoardId(@PathVariable("boardId") Long boardId,
                                         @AuthenticationPrincipal @Nullable UserDetailsImpl userDetails) {
        String gitHubId = userDetails != null ? userDetails.getUsername() : null;
        Boolean result = likeService.getLikeByBoardId(boardId, gitHubId);
        return new RestResponse(result);
    }

    @GetMapping("/popular-week/members")
    public RestResponse getPopularWeekMember() {
        return new RestResponse(likeService.getPopularWeekMember());
    }

    @GetMapping("/popular-week/repositories")
    public RestResponse getPopularWeekRepository() {
        return new RestResponse(likeService.getPopularWeekRepository());
    }

//    @GetMapping("/{boardId}")
//    public RestResponse getLikesByBoardId(@PathVariable("boardId") Long boardId,
//                                          @RequestParam(value = "page", defaultValue = "0") int page,
//                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
//        PagingResponse<List<LikesRespDto>> result = likeService.getLikesByBoardId(boardId, page, size);
//        return new RestResponse(result);
//    }
}
