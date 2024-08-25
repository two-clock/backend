package com.twoclock.gitconnect.domain.comment.web;

import com.twoclock.gitconnect.domain.comment.dto.CommentListRespDto;
import com.twoclock.gitconnect.domain.comment.dto.CommentModifyReqDto;
import com.twoclock.gitconnect.domain.comment.dto.CommentRegistReqDto;
import com.twoclock.gitconnect.domain.comment.service.CommentService;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/boards/{boardId}/comments")
    public RestResponse saveComment(@RequestBody @Valid CommentRegistReqDto dto,
                                    @PathVariable("boardId") Long boardId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        commentService.saveComment(dto, githubId, boardId);
        return RestResponse.OK();
    }

    @PutMapping("/comments/{commentId}")
    public RestResponse modifyComment(@RequestBody @Valid CommentModifyReqDto dto,
                                      @PathVariable("commentId") Long commentId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        commentService.modifyComment(dto, githubId, commentId);
        return RestResponse.OK();
    }

    @DeleteMapping("/comments/{commentId}")
    public RestResponse deleteComment(@PathVariable("commentId") Long commentId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        commentService.deleteComment(githubId, commentId);
        return RestResponse.OK();
    }

    @GetMapping("/boards/{boardId}/comments")
    public RestResponse getComments(@PathVariable("boardId") Long boardId) {
        List<CommentListRespDto> result = commentService.getComments(boardId);
        return new RestResponse(result);
    }
}
