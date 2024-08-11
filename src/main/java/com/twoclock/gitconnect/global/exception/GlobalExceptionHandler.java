package com.twoclock.gitconnect.global.exception;

import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.slack.annotation.SlackNotification;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> MethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponseDto responseDto =
                new ErrorResponseDto(
                        ErrorCode.BAD_REQUEST.getCode(),
                        ErrorCode.BAD_REQUEST.getMessage(),
                        null
                );

        log.error(e.getMessage(), e);

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> AccessDeniedException(AccessDeniedException e) {
        ErrorResponseDto responseDto =
                new ErrorResponseDto(
                        ErrorCode.BAD_REQUEST.getCode(),
                        ErrorCode.BAD_REQUEST.getMessage(),
                        null
                );

        log.error(e.getMessage(), e);

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponseDto> MissingRequestCookieException(MissingRequestCookieException e) {
        ErrorResponseDto responseDto =
                new ErrorResponseDto(
                        ErrorCode.NOT_ACCEPTABLE.getCode(),
                        ErrorCode.NOT_ACCEPTABLE.getMessage(),
                        null
                );

        log.error(e.getMessage(), e);

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
