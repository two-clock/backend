package com.twoclock.gitconnect.global.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Rest API 응답을 위한 클래스
 * 2XX httpStatus 성공 경우에만 반환
 */

@Builder
public record HttpResponse<T>(
        int httpStatus,
        String message,
        T data

) {


}
