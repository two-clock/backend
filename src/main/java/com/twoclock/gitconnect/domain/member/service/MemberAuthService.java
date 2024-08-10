package com.twoclock.gitconnect.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.domain.member.dto.MemberInfoDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.constants.GitHubUri;
import com.twoclock.gitconnect.global.jwt.JwtService;
import com.twoclock.gitconnect.global.jwt.dto.JwtTokenInfoDto;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import com.twoclock.gitconnect.global.util.RestClientUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class MemberAuthService {

    private static final String APPLICATION_FORM_URLENCODED_CHARSET_UTF8 = "application/x-www-form-urlencoded;charset=utf-8";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    public MemberInfoDto githubLogin(String code, HttpServletResponse httpServletResponse) {
        String gitHubAccessToken = getGitHubAccessToken(code);
        MemberInfoDto memberInfoDto = getGitHubMemberInfo(gitHubAccessToken);
        Member member = registerOrUpdateMember(memberInfoDto);
        JwtTokenInfoDto jwtTokenInfoDto = forceLogin(member);

        String accessToken = jwtTokenInfoDto.accessToken();
        String refreshToken = jwtTokenInfoDto.refreshToken();

        setAuthJwtTokens(httpServletResponse, member, accessToken, refreshToken);
        return new MemberInfoDto(member.getLogin(), member.getAvatarUrl(), member.getName());
    }

    private String getGitHubAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private MemberInfoDto getGitHubMemberInfo(String gitHubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Authorization", "Bearer " + gitHubAccessToken);
        headers.add("Accept", "application/vnd.github+json");

        String result = RestClientUtil.get(GitHubUri.USER_INFO.getUri(), headers);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String login = jsonNode.get("login").asText();
            String avatarUrl = jsonNode.get("avatar_url").asText();
            String name = jsonNode.get("name").asText();
            return new MemberInfoDto(login, avatarUrl, name);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Member registerOrUpdateMember(MemberInfoDto memberInfoDto) {
        String token = memberInfoDto.login();
        String avatarUrl = memberInfoDto.avatarUrl();
        String name = memberInfoDto.name();

        return memberRepository.findByLogin(token).map(member -> {
            member.update(token, avatarUrl, name);
            return member;
        }).orElseGet(() -> {
            Member member = Member.builder()
                    .login(token)
                    .avatarUrl(avatarUrl)
                    .name(name)
                    .role(Role.ROLE_USER)
                    .build();

            memberRepository.save(member);
            return member;
        });
    }

    private JwtTokenInfoDto forceLogin(Member member) {
        UserDetails userDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtAccessToken = jwtService.generateAccessToken(member);
        String jwtRefreshToken = jwtService.generateRefreshToken(member);
        return new JwtTokenInfoDto(jwtAccessToken, jwtRefreshToken);
    }

    private void setAuthJwtTokens(HttpServletResponse httpServletResponse, Member member, String accessToken, String refreshToken) {
        httpServletResponse.addHeader(HttpHeaders.AUTHORIZATION, JwtService.BEARER_PREFIX + accessToken);
        redisTemplate.opsForValue()
                .set(member.getLogin(), refreshToken, JwtService.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(JwtService.REFRESH_TOKEN_EXPIRATION_TIME);
        httpServletResponse.addCookie(refreshCookie);
    }
}
