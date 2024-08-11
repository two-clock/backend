package com.twoclock.gitconnect.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.global.exception.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public class CustomResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void fail(
            HttpServletResponse response, HttpStatus status, String code, String message
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(code, message, null);

        String responseJson = objectMapper.writeValueAsString(errorResponseDto);
        response.getWriter().write(responseJson);
    }
}
