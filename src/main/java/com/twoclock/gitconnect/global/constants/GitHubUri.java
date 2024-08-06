package com.twoclock.gitconnect.global.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GitHubUri {

    ACCESS_TOKEN("https://github.com/login/oauth/access_token?scope=user"),
    USER_INFO("https://api.github.com/user");

    private final String uri;
}
