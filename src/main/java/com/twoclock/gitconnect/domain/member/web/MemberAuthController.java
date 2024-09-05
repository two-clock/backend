package com.twoclock.gitconnect.domain.member.web;

import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import com.twoclock.gitconnect.domain.member.service.MemberAuthService;
import com.twoclock.gitconnect.global.jwt.service.JwtService;
import com.twoclock.gitconnect.global.model.RestResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members/auth")
@RestController
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @GetMapping("/github/callback")
    public RestResponse githubLogin(@RequestParam String code, HttpServletResponse httpServletResponse) {
        MemberLoginRespDto responseDto = memberAuthService.githubLogin(code, httpServletResponse);
        return new RestResponse(responseDto);
    }

    @PostMapping("/refresh")
    public RestResponse refreshJwtToken(
            @CookieValue(name = JwtService.JWT_REFRESH_TOKEN_KEY) String refreshToken,
            HttpServletResponse httpServletResponse
    ) {
        memberAuthService.refreshJwtToken(refreshToken, httpServletResponse);
        return RestResponse.OK();
    }

    @PostMapping("/logout")
    public RestResponse logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        memberAuthService.logout(httpServletRequest, httpServletResponse);
        return RestResponse.OK();
    }
}
