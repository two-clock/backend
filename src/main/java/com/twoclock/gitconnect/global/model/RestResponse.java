package com.twoclock.gitconnect.global.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * Rest API 응답을 위한 클래스
 * 2XX httpStatus 성공 경우에만 반환
 */
public class RestResponse extends ResponseEntity<HttpResponse<?>> {

    public RestResponse(HttpResponse body, HttpStatusCode status) {
        super(body, status);
    }

    // 성공 응답(데이터 반환X)
    public static RestResponse OK() {
        // no data return
        HttpResponse<?> response = HttpResponse.builder()
                .message(HttpStatus.OK.getReasonPhrase())
                .build();
        return new RestResponse(response, HttpStatus.OK);
    }

    // 성공 응답 데이터 반환처리
    public RestResponse(Object resultData) {
        super(HttpResponse.builder()
                .message("OK")
                .data(resultData)
                .build(), HttpStatus.OK);
    }

    // 성공 응답(헤더 반환처리)
    public RestResponse(Object resultData, HttpHeaders headers) {
        super(HttpResponse.builder()
                .message("OK")
                .data(resultData)
                .build(), headers, HttpStatus.OK);
    }
}
