package com.twoclock.gitconnect.openapi.github.web;

import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
import com.twoclock.gitconnect.openapi.github.service.GithubAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/github")
@RestController
public class GithubAPIController {

    private final GithubAPIService githubAPIService;

    // 임시용
    @Value("${github.access-token}")
    private String accessToken;

    @GetMapping("/followers")
    public RestResponse getFollowers() {
        // TODO : 토큰에서 Github Access Token을 추출 해야함.
        List<FollowRespDto> result = githubAPIService.getFollowers(accessToken);
        return new RestResponse(result);
    }

    @GetMapping("/following")
    public RestResponse getFollowing() {
        // TODO : 토큰에서 Github Access Token을 추출 해야함.
        List<FollowRespDto> result = githubAPIService.getFollowing(accessToken);
        return new RestResponse(result);
    }
}
