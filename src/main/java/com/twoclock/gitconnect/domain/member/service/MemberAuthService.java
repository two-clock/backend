package com.twoclock.gitconnect.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.domain.member.dto.MemberGitHubTokenDto;
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

@RequiredArgsConstructor
@Service
public class MemberAuthService {

    private static final String APPLICATION_FORM_URLENCODED_CHARSET_UTF8 = "application/x-www-form-urlencoded;charset=utf-8";
    private static final String APPLICATION_VND_GITHUB_JSON = "application/vnd.github+json";

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final JwtRedisService jwtRedisService;
    private final MemberAuthRedisService memberAuthRedisService;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    public MemberInfoDto githubLogin(String code, HttpServletResponse httpServletResponse) {
        MemberGitHubTokenDto memberGitHubTokenDto = getMemberGitHubToken(code);
        String gitHubAccessToken = memberGitHubTokenDto.accessToken();

        MemberInfoDto memberInfoDto = getGitHubMemberInfo(gitHubAccessToken);
        Member member = registerOrUpdateMember(memberInfoDto);

        deleteRefreshTokenIfExist(member.getGitHubId());
        JwtTokenInfoDto jwtTokenInfoDto = forceLogin(member);

        setAuthJwtTokens(httpServletResponse, member, jwtTokenInfoDto, memberGitHubTokenDto);
        return new MemberInfoDto(member.getLogin(), member.getGitHubId(), member.getAvatarUrl(), member.getName());
    }

    public void refreshJwtToken(String refreshToken, HttpServletResponse httpServletResponse) {
        String gitHubId = jwtService.getGitHubId(refreshToken);
        Member member = memberRepository.findByGitHubId(gitHubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        String savedRefreshToken = jwtRedisService.getRefreshToken(gitHubId);
        if (!refreshToken.equals(savedRefreshToken)) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_ERROR);
        }

        String gitHubRefreshToken = memberAuthRedisService.getGitHubToken(gitHubId).refreshToken();
        MemberGitHubTokenDto memberGitHubTokenDto = refreshMemberGitHubToken(gitHubRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(member);
        String newRefreshToken = jwtService.generateRefreshToken(member);
        JwtTokenInfoDto jwtTokenInfoDto = new JwtTokenInfoDto(newAccessToken, newRefreshToken);

        setAuthJwtTokens(httpServletResponse, member, jwtTokenInfoDto, memberGitHubTokenDto);
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String jwtAccessToken = authorization.substring(JwtService.BEARER_PREFIX.length());
        String gitHubId = jwtService.getGitHubId(jwtAccessToken);

        long jwtAccessTokenExpirationTime = jwtService.getTokenExpirationTime(jwtAccessToken);
        long now = new Date().getTime();

        jwtRedisService.deleteRefreshToken(gitHubId);
        jwtRedisService.addToBlacklist(jwtAccessToken, jwtAccessTokenExpirationTime - now);

        Cookie deleteRefreshCookie = new Cookie("refreshToken", null);
        deleteRefreshCookie.setHttpOnly(true);
        deleteRefreshCookie.setPath("/");
        deleteRefreshCookie.setMaxAge(0);
        httpServletResponse.addCookie(deleteRefreshCookie);

        String gitHubAccessToken = memberAuthRedisService.getGitHubToken(gitHubId).accessToken();
        deleteMemberGitHubToken(gitHubAccessToken);
        memberAuthRedisService.deleteGitHubToken(gitHubId);
    }

    private MemberGitHubTokenDto getMemberGitHubToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();
            return new MemberGitHubTokenDto(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private MemberGitHubTokenDto refreshMemberGitHubToken(String gitHubRefreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", gitHubRefreshToken);

        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();
            return new MemberGitHubTokenDto(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMemberGitHubToken(String gitHubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);
        headers.setBasicAuth(clientId, clientSecret);

        String body = "{ \"access_token\": \"" + gitHubAccessToken + "\" }";

        RestClientUtil.delete(String.format(GitHubUri.DELETE_TOKEN.getUri(), clientId), headers, body);
    }

    private MemberInfoDto getGitHubMemberInfo(String gitHubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Authorization", "Bearer " + gitHubAccessToken);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);

        String result = RestClientUtil.get(GitHubUri.USER_INFO.getUri(), headers);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String login = jsonNode.get("login").asText();
            String gitHubId = jsonNode.get("id").asText();
            String avatarUrl = jsonNode.get("avatar_url").asText();
            String name = jsonNode.get("name").asText();
            return new MemberInfoDto(login, gitHubId, avatarUrl, name);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Member registerOrUpdateMember(MemberInfoDto memberInfoDto) {
        String login = memberInfoDto.login();
        String gitHubId = memberInfoDto.gitHubId();
        String avatarUrl = memberInfoDto.avatarUrl();
        String name = memberInfoDto.name();

        return memberRepository.findByLogin(login).map(member -> {
            member.update(login, avatarUrl, name);
            return member;
        }).orElseGet(() -> {
            Member member = Member.builder()
                    .login(login)
                    .gitHubId(gitHubId)
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

    private void setAuthJwtTokens(
            HttpServletResponse httpServletResponse,
            Member member,
            JwtTokenInfoDto jwtTokenInfoDto,
            MemberGitHubTokenDto memberGitHubTokenDto
    ) {
        String accessToken = jwtTokenInfoDto.accessToken();
        String refreshToken = jwtTokenInfoDto.refreshToken();

        httpServletResponse.addHeader(HttpHeaders.AUTHORIZATION, JwtService.BEARER_PREFIX + accessToken);
        jwtRedisService.saveRefreshToken(member.getGitHubId(), refreshToken, JwtService.REFRESH_TOKEN_EXPIRATION_TIME);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(JwtService.REFRESH_TOKEN_EXPIRATION_TIME);
        httpServletResponse.addCookie(refreshCookie);

        String gitHubAccessToken = memberGitHubTokenDto.accessToken();
        String gitHubRefreshToken = memberGitHubTokenDto.refreshToken();

        memberAuthRedisService.saveGitHubToken(member.getGitHubId(), gitHubAccessToken, gitHubRefreshToken);
    }

    private void deleteRefreshTokenIfExist(String login) {
        if (jwtRedisService.getRefreshToken(login) != null) {
            jwtRedisService.deleteRefreshToken(login);
        }
    }
}
