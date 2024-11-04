package com.twoclock.gitconnect.openapi.github.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
@Getter
public enum GitHubUri {

    ACCESS_TOKEN(HttpMethod.POST, "https://github.com/login/oauth/access_token?scope=user"),
    USER_INFO(HttpMethod.GET, "https://api.github.com/user"),
    GET_USER_INFO(HttpMethod.GET, "https://api.github.com/users/%s"),
    DELETE_TOKEN(HttpMethod.POST, "https://api.github.com/applications/%s/token"),
    FOLLOWER_LIST(HttpMethod.GET, "https://api.github.com/user/followers"),
    FOLLOWING_LIST(HttpMethod.GET, "https://api.github.com/user/following"),
    REPOSITORY_LIST(HttpMethod.GET, "https://api.github.com/user/repos?sort=created&per_page=100");

    private final HttpMethod method;
    private final String uri;
}
