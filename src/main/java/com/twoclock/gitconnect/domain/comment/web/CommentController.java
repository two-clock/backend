package com.twoclock.gitconnect.domain.comment.web;

import com.twoclock.gitconnect.domain.comment.dto.CommentRegistReqDto;
import com.twoclock.gitconnect.domain.comment.service.CommentService;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public RestResponse saveComment(@RequestBody @Valid CommentRegistReqDto dto,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        commentService.saveComment(dto, githubId);
        return RestResponse.OK();
    }
}
