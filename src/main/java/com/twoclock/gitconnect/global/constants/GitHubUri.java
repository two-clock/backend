package com.twoclock.gitconnect.global.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
@Getter
public enum GitHubUri {

    ACCESS_TOKEN(HttpMethod.POST, "https://github.com/login/oauth/access_token?scope=user"),
    USER_INFO(HttpMethod.GET, "https://api.github.com/user"),
    FOLLOWER_LIST(HttpMethod.GET, "https://api.github.com/user/followers"),
    FOLLOWING_LIST(HttpMethod.GET, "https://api.github.com/user/following");

    private final HttpMethod method;
    private final String uri;
}
