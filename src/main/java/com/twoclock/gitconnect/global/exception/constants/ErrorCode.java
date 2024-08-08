package com.twoclock.gitconnect.global.exception.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "서버 에러가 발생했습니다."),

    JWT_ERROR(HttpStatus.UNAUTHORIZED, "JWT-001", "JWT 토큰 에러가 발생했습니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "M-001", "찾을 수 없는 회원 계정입니다."),
    DIFF_USER_BOARD(HttpStatus.FORBIDDEN, "M-002", "게시글을 등록한 사용자와 일치하지 않습니다."),

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "C-001", "찾을 수 없는 카테고리입니다."),

    GITHUB_SERVER_ERROR(HttpStatus.BAD_REQUEST, "G-001", "GitHub 서버 에러가 발생했습니다."),

    FAIL_SAVE_BOARD(HttpStatus.INTERNAL_SERVER_ERROR, "B-001", "게시글 저장에 실패하였습니다."),
    FAIL_MODIFY_BOARD(HttpStatus.INTERNAL_SERVER_ERROR, "B-002", "게시글 수정에 실패하였습니다."),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "B-003", "해당 게시판을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
