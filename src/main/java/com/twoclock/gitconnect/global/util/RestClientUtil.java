package com.twoclock.gitconnect.global.util;

import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class RestClientUtil {

    private static final RestClient restClient = RestClient.create();

    public static String post(String uri, HttpHeaders headers, MultiValueMap<String, String> body) {
        return restClient.post()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new CustomException(ErrorCode.GITHUB_SERVER_ERROR);
                }))
                .body(String.class);
    }

    public static String get(String uri, HttpHeaders headers) {
        return restClient.get()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new CustomException(ErrorCode.GITHUB_SERVER_ERROR);
                }))
                .body(String.class);
    }

    public static void delete(String uri, HttpHeaders headers, String body) {
        restClient.method(HttpMethod.DELETE)
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new CustomException(ErrorCode.GITHUB_SERVER_ERROR);
                }))
                .toBodilessEntity();
    }
}
