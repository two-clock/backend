package com.twoclock.gitconnect.global.model;

import lombok.Builder;

/**
 * Rest API 응답을 위한 클래스
 * 2XX httpStatus 성공 경우에만 반환
 */

@Builder
public record HttpResponse<T>(
        String message,
        T data
) {

}
