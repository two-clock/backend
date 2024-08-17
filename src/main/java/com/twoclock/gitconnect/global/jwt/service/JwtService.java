package com.twoclock.gitconnect.global.jwt.service;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.global.jwt.dto.JwtTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    private static final int ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30분
    public static final int REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 일주일

    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String BEARER_PREFIX = "Bearer ";

    public String generateAccessToken(Member member) {
        String subject = member.getGitHubId();

        Map<String, String> claims = new HashMap<>();
        claims.put("login", member.getLogin());
        claims.put("avatarUrl", member.getAvatarUrl());
        claims.put("name", member.getName());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .signWith(SECRET_KEY)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        String subject = member.getGitHubId();

        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(subject)
                .signWith(SECRET_KEY)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        if (!StringUtils.hasText(jwtToken)) {
            return false;
        }
        return getClaims(jwtToken).getExpiration().after(new Date());
    }

    public String getGitHubId(String jwtToken) {
        return getClaims(jwtToken).getSubject();
    }

    public long getTokenExpirationTime(String jwtToken) {
        return getClaims(jwtToken).getExpiration()
                .getTime();
    }

    public JwtTokenDto createJwtTokens(Member member) {
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken(member);
        return new JwtTokenDto(accessToken, refreshToken);
    }

    private Claims getClaims(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            log.error("JWT token validation failed", e);
            throw new JwtException("Invalid JWT signature", e);
        }
    }
}
