package com.twoclock.gitconnect.global.exception.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "서버 에러가 발생했습니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "U-001", "잘못된 요청입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "U-002", "요청을 수락할 수 없습니다."),

    JWT_ERROR(HttpStatus.UNAUTHORIZED, "JWT-001", "JWT 토큰 에러가 발생했습니다."),
    JWT_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN, "JWT-002", "JWT 리프레쉬 토큰 에러가 발생했습니다."),
    JWT_BLACKLIST(HttpStatus.FORBIDDEN, "JWT-003", "블랙 리스트에 저장된 JWT 토큰입니다."),
    JWT_EXPIRED(HttpStatus.FORBIDDEN, "JWT-004", "만료 기한이 지난 JWT 토큰입니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "M-001", "찾을 수 없는 회원 계정입니다."),
    DIFF_USER_BOARD(HttpStatus.FORBIDDEN, "M-002", "게시글을 등록한 사용자와 일치하지 않습니다."),

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "C-001", "찾을 수 없는 카테고리입니다."),

    OPEN_API_SERVER_ERROR(HttpStatus.BAD_REQUEST, "G-001", "Open API 서버 에러가 발생했습니다."),

    FAIL_SAVE_BOARD(HttpStatus.INTERNAL_SERVER_ERROR, "B-001", "게시글 저장에 실패하였습니다."),
    FAIL_MODIFY_BOARD(HttpStatus.INTERNAL_SERVER_ERROR, "B-002", "게시글 수정에 실패하였습니다."),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "B-003", "해당 게시판을 찾을 수 없습니다."),
    MANY_SAVE_REQUEST_BOARD(HttpStatus.TOO_MANY_REQUESTS, "B-004", "게시글은 5분에 1번 작성할 수 있습니다."),

    DUPLICATED_LIKE(HttpStatus.BAD_REQUEST, "L-001", "이미 좋아요를 누른 게시글입니다."),
    NOT_FOUND_LIKE(HttpStatus.BAD_REQUEST, "L-002", "좋아요를 누른 게시글을 찾을 수 없습니다."),

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "CM-001", "찾을 수 없는 댓글입니다."),

    BAD_WORD(HttpStatus.BAD_REQUEST, "W-001", "금지어가 포함되어 있습니다."),

    ALREADY_EXIST_CHAT_ROOM(HttpStatus.CONFLICT, "CH-001", "이미 존재하는 채팅방입니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "CH-002", "찾을 수 없는 채팅방입니다."),
    NO_ACCESS_CHAT_ROOM(HttpStatus.BAD_REQUEST, "CH-003", "접근할 수 없는 채팅방입니다."),
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
