package com.twoclock.gitconnect.global.exception;

import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.slack.annotation.SlackNotification;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(CustomException e) {
        ErrorResponseDto responseDto = new ErrorResponseDto(e.getErrorCode().getCode(), e.getMessage(), null);

        log.error(e.getMessage(), e);

        return new ResponseEntity<>(responseDto, e.getErrorCode().getHttpStatus());
    }

    @SlackNotification
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(HttpServletRequest request, Exception e) {
        ErrorResponseDto responseDto =
                new ErrorResponseDto(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                        null
                );

        log.error(e.getMessage(), e);

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
