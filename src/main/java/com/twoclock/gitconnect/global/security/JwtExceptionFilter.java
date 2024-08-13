package com.twoclock.gitconnect.global.security;

import com.twoclock.gitconnect.global.util.CustomResponseUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.twoclock.gitconnect.global.exception.constants.ErrorCode.JWT_ERROR;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            CustomResponseUtil.fail(response, JWT_ERROR.getHttpStatus(), JWT_ERROR.getCode(), JWT_ERROR.getMessage());
        }
    }
}
