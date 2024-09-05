package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.jwt.dto.JwtTokenDto;
import com.twoclock.gitconnect.global.jwt.service.JwtRedisService;
import com.twoclock.gitconnect.global.jwt.service.JwtService;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import com.twoclock.gitconnect.global.util.CookieUtil;
import com.twoclock.gitconnect.openapi.github.dto.GitHubTokenDto;
import com.twoclock.gitconnect.openapi.github.service.GitHubTokenRedisService;
import com.twoclock.gitconnect.openapi.github.service.GithubAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final JwtRedisService jwtRedisService;
    private final GitHubTokenRedisService gitHubTokenRedisService;
    private final GithubAPIService githubAPIService;

    public MemberLoginRespDto githubLogin(String code, HttpServletResponse response) {
        GitHubTokenDto gitHubTokenDto = githubAPIService.getMemberGitHubToken(code);
        MemberLoginRespDto memberLoginRespDto = githubAPIService.getGitHubMember(gitHubTokenDto.accessToken());

        Member member = registerOrUpdateMember(memberLoginRespDto);
        handleExistingRefreshToken(member.getGitHubId());

        JwtTokenDto jwtTokenDto = authenticateMember(member);
        setAuthTokens(response, member, jwtTokenDto, gitHubTokenDto);
        return buildMemberLoginRespDto(member);
    }

    public void refreshJwtToken(String refreshToken, HttpServletResponse response) {
        String gitHubId = jwtService.getGitHubId(refreshToken);
        Member member = memberRepository.findByGitHubId(gitHubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        validateRefreshToken(gitHubId, refreshToken);
        GitHubTokenDto gitHubTokenDto = refreshGitHubToken(gitHubId);

        JwtTokenDto newJwtTokenDto = jwtService.createJwtTokens(member);
        setAuthTokens(response, member, newJwtTokenDto, gitHubTokenDto);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String jwtAccessToken = extractAccessToken(request);
        String gitHubId = jwtService.getGitHubId(jwtAccessToken);

        blacklistToken(jwtAccessToken);
        jwtRedisService.deleteRefreshToken(gitHubId);

        CookieUtil.deleteCookie(response, JwtService.JWT_REFRESH_TOKEN_KEY);
        deleteGitHubTokens(gitHubId);
    }

    private Member registerOrUpdateMember(MemberLoginRespDto memberLoginRespDto) {
        return memberRepository.findByLogin(memberLoginRespDto.login())
                .map(member -> updateMember(member, memberLoginRespDto))
                .orElseGet(() -> createNewMember(memberLoginRespDto));
    }

    private Member updateMember(Member member, MemberLoginRespDto memberLoginRespDto) {
        member.update(memberLoginRespDto.login(), memberLoginRespDto.avatarUrl(), memberLoginRespDto.name());
        return member;
    }

    private Member createNewMember(MemberLoginRespDto memberLoginRespDto) {
        Member member = Member.builder()
                .login(memberLoginRespDto.login())
                .gitHubId(memberLoginRespDto.gitHubId())
                .avatarUrl(memberLoginRespDto.avatarUrl())
                .name(memberLoginRespDto.name())
                .role(Role.ROLE_USER)
                .build();
        return memberRepository.save(member);
    }

    private void handleExistingRefreshToken(String gitHubId) {
        if (jwtRedisService.getRefreshToken(gitHubId) != null) {
            jwtRedisService.deleteRefreshToken(gitHubId);
        }
    }

    private JwtTokenDto authenticateMember(Member member) {
        UserDetails userDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtService.createJwtTokens(member);
    }

    private void setAuthTokens(HttpServletResponse response, Member member, JwtTokenDto jwtTokenDto, GitHubTokenDto gitHubTokenDto) {
        setAuthorizationHeader(response, jwtTokenDto.accessToken());
        CookieUtil.addCookie(response, JwtService.JWT_REFRESH_TOKEN_KEY, jwtTokenDto.refreshToken(), JwtService.REFRESH_TOKEN_EXPIRATION_TIME);
        saveTokensToRedis(member, jwtTokenDto, gitHubTokenDto);
    }

    private void setAuthorizationHeader(HttpServletResponse response, String accessToken) {
        response.addHeader(HttpHeaders.AUTHORIZATION, JwtService.BEARER_PREFIX + accessToken);
    }

    private void saveTokensToRedis(Member member, JwtTokenDto jwtTokenDto, GitHubTokenDto gitHubTokenDto) {
        jwtRedisService.saveRefreshToken(member.getGitHubId(), jwtTokenDto.refreshToken(), JwtService.REFRESH_TOKEN_EXPIRATION_TIME);
        gitHubTokenRedisService.saveGitHubToken(member.getGitHubId(), gitHubTokenDto.accessToken(), gitHubTokenDto.refreshToken());
    }

    private void validateRefreshToken(String gitHubId, String refreshToken) {
        String savedRefreshToken = jwtRedisService.getRefreshToken(gitHubId);
        if (!refreshToken.equals(savedRefreshToken)) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_ERROR);
        }
    }

    private GitHubTokenDto refreshGitHubToken(String gitHubId) {
        String gitHubRefreshToken = gitHubTokenRedisService.getGitHubToken(gitHubId).refreshToken();
        return githubAPIService.refreshMemberGitHubToken(gitHubRefreshToken);
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorization.substring(JwtService.BEARER_PREFIX.length());
    }

    private void blacklistToken(String jwtAccessToken) {
        long expirationTime = jwtService.getTokenExpirationTime(jwtAccessToken);
        long now = new Date().getTime();

        if (expirationTime < now) {
            return;
        }

        jwtRedisService.addToBlacklist(jwtAccessToken, expirationTime - now);
    }

    private void deleteGitHubTokens(String gitHubId) {
        String gitHubAccessToken = gitHubTokenRedisService.getGitHubToken(gitHubId).accessToken();
        githubAPIService.deleteMemberGitHubToken(gitHubAccessToken);
        gitHubTokenRedisService.deleteGitHubToken(gitHubId);
    }

    private MemberLoginRespDto buildMemberLoginRespDto(Member member) {
        return new MemberLoginRespDto(member.getLogin(), member.getGitHubId(), member.getAvatarUrl(), member.getName());
    }
}
