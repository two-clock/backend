package com.twoclock.gitconnect.domain.member.web;

import com.twoclock.gitconnect.domain.member.dto.MemberInfoDto;
import com.twoclock.gitconnect.domain.member.service.MemberAuthService;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members/auth")
@RestController
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @GetMapping("/github/callback")
    public RestResponse githubLogin(@RequestParam String code, HttpServletResponse httpServletResponse) {
        MemberInfoDto responseDto = memberAuthService.githubLogin(code, httpServletResponse);
        return new RestResponse(responseDto);
    }
}
