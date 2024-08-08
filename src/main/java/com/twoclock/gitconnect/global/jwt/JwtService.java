package com.twoclock.gitconnect.global.jwt;

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
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String BEARER_PREFIX = "Bearer ";

    public String generateAccessToken(Member member) {
        return generateToken(member.getLogin());
    }

    public String getLogin(String accessToken) {
        return getSubject(accessToken);
    }

    private String generateToken(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(subject)
                .signWith(SECRET_KEY)
                .issuedAt(now)
                .expiration(expiration)
                .compact();
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
