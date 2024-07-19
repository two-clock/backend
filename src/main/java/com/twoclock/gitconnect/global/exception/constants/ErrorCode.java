package com.twoclock.gitconnect.global.exception.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "서버 에러가 발생했습니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "M-001", "찾을 수 없는 회원 계정입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
