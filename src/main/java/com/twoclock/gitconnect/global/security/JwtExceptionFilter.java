package com.twoclock.gitconnect.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.global.exception.ErrorResponseDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
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
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(JWT_ERROR.getHttpStatus().value());
            response.setCharacterEncoding("UTF-8");

            ErrorResponseDto errorResponseDto =
                    new ErrorResponseDto(JWT_ERROR.getCode(), JWT_ERROR.getMessage(), null);

            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(errorResponseDto);
            response.getWriter().write(responseJson);
        }
    }
}
