package com.twoclock.gitconnect.domain.member.web;

import com.twoclock.gitconnect.domain.member.dto.MemberProfileRespDto;
import com.twoclock.gitconnect.domain.member.service.MemberService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public RestResponse getMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String gitHubId = userDetails.getUsername();
        MemberProfileRespDto responseDto = memberService.getMember(gitHubId);
        return new RestResponse(responseDto);
    }
}
