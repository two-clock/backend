package com.twoclock.gitconnect.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.domain.member.dto.MemberInfoDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.constants.GitHubUri;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.jwt.dto.JwtTokenInfoDto;
import com.twoclock.gitconnect.global.jwt.service.JwtRedisService;
import com.twoclock.gitconnect.global.jwt.service.JwtService;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import com.twoclock.gitconnect.global.util.RestClientUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberAuthService {

    private static final String APPLICATION_FORM_URLENCODED_CHARSET_UTF8 = "application/x-www-form-urlencoded;charset=utf-8";

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final JwtRedisService jwtRedisService;

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

        deleteRefreshTokenIfExist(member.getLogin());

        JwtTokenInfoDto jwtTokenInfoDto = forceLogin(member);
        String accessToken = jwtTokenInfoDto.accessToken();
        String refreshToken = jwtTokenInfoDto.refreshToken();

        setAuthJwtTokens(httpServletResponse, member, accessToken, refreshToken);
        return new MemberInfoDto(member.getLogin(), member.getAvatarUrl(), member.getName());
    }

    public void refreshJwtToken(String refreshToken, HttpServletResponse httpServletResponse) {
        String login = jwtService.getLogin(refreshToken);
        Member member = memberRepository.findByLogin(login)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        String savedRefreshToken = jwtRedisService.getRefreshToken(login);
        if (!refreshToken.equals(savedRefreshToken)) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_ERROR);
        }

        String newAccessToken = jwtService.generateAccessToken(member);
        String newRefreshToken = jwtService.generateRefreshToken(member);

        setAuthJwtTokens(httpServletResponse, member, newAccessToken, newRefreshToken);
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String jwtAccessToken = authorization.substring(JwtService.BEARER_PREFIX.length());
        String login = jwtService.getLogin(jwtAccessToken);

        long jwtAccessTokenExpirationTime = jwtService.getTokenExpirationTime(jwtAccessToken);
        long now = new Date().getTime();

        jwtRedisService.deleteRefreshToken(login);
        jwtRedisService.addToBlacklist(jwtAccessToken, jwtAccessTokenExpirationTime - now);

        Cookie deleteRefreshCookie = new Cookie("refreshToken", null);
        deleteRefreshCookie.setHttpOnly(true);
        deleteRefreshCookie.setPath("/");
        deleteRefreshCookie.setMaxAge(0);
        httpServletResponse.addCookie(deleteRefreshCookie);
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
        jwtRedisService.saveRefreshToken(member.getLogin(), refreshToken, JwtService.REFRESH_TOKEN_EXPIRATION_TIME);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(JwtService.REFRESH_TOKEN_EXPIRATION_TIME);
        httpServletResponse.addCookie(refreshCookie);
    }

    private void deleteRefreshTokenIfExist(String login) {
        if (jwtRedisService.getRefreshToken(login) != null) {
            jwtRedisService.deleteRefreshToken(login);
        }
    }
}
