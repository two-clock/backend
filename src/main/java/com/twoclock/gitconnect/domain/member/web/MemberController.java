package com.twoclock.gitconnect.domain.member.web;

import com.twoclock.gitconnect.domain.member.dto.MyProfileRespDto;
import com.twoclock.gitconnect.domain.member.service.MemberService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import com.twoclock.gitconnect.openapi.github.dto.MemberInfoRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public RestResponse getMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String gitHubId = userDetails.getUsername();
        MyProfileRespDto responseDto = memberService.getMyInfo(gitHubId);
        return new RestResponse(responseDto);
    }

    @GetMapping("/{userGitHubId}")
    public RestResponse getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @PathVariable String userGitHubId) {
        String gitHubId = userDetails.getUsername();
        MemberInfoRespDto responseDto = memberService.getMemberInfo(gitHubId, userGitHubId);
        return new RestResponse(responseDto);
    }

    @PostMapping("/agree")
    public RestResponse useServiceAgree(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String gitHubId = userDetails.getUsername();
        memberService.useServiceAgree(gitHubId);
        return RestResponse.OK();
    }
}
