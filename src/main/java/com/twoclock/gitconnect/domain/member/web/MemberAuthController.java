package com.twoclock.gitconnect.domain.member.web;

import com.twoclock.gitconnect.domain.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> githubLogin(@RequestParam String code) {
        memberAuthService.githubLogin(code);
        // TODO: 로그인 시 AccessToken 반환
        return null;
    }
}
