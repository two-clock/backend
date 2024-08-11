package com.twoclock.gitconnect.global.jwt.service;

import com.twoclock.gitconnect.domain.member.entity.Member;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private static final int ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000; // 30분
    public static final int REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 일주일

    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String BEARER_PREFIX = "Bearer ";

    public String generateAccessToken(Member member) {
        String subject = member.getLogin();
        String avatarUrl = member.getAvatarUrl();
        String name = member.getName();

        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(subject)
                .claim("avatarUrl", avatarUrl)
                .claim("name", name)
                .signWith(SECRET_KEY)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        String subject = member.getLogin();

        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(subject)
                .signWith(SECRET_KEY)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }

    public String getLogin(String jwtToken) {
        return getSubject(jwtToken);
    }

    private String getSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

        } catch (JwtException e) {
            log.error("JWT token validation failed", e);
            throw new JwtException("Invalid JWT signature", e);
        }
    }
}
