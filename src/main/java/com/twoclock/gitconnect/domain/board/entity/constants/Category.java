package com.twoclock.gitconnect.domain.board.entity.constants;

import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;

public enum Category {

    BD1, // 계정 홍보 게시글
    BD2, // Repository 홍보 게시글
    BD3; // 신고 게시글


    // 카테고리 유효성 검사
    public static Category of(String category) {
        try {
            return Category.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_CATEGORY);
        }
    }
}
