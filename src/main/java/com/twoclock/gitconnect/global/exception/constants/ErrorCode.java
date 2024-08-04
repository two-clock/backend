package com.twoclock.gitconnect.global.exception.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "서버 에러가 발생했습니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "M-001", "찾을 수 없는 회원 계정입니다."),

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "C-001", "찾을 수 없는 카테고리입니다."),

    FAIL_SAVE_BOARD(HttpStatus.NOT_FOUND, "B-001", "게시글 저장에 실패하였습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
