package com.twoclock.gitconnect.domain.member.entity.constants;

import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;

public enum Role {

    ROLE_ADMIN, ROLE_USER;

    public static Role of(String role) {
        try {
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_ROLE);
        }
    }
}
