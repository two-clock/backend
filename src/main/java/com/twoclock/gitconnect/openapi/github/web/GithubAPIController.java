package com.twoclock.gitconnect.openapi.github.web;

import com.twoclock.gitconnect.openapi.github.service.GithubAPISerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/github")
@RestController
public class GithubAPIController {

    private final GithubAPISerivce githubAPISerivce;
}
