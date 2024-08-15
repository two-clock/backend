package com.twoclock.gitconnect.openapi.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.global.constants.GitHubUri;
import com.twoclock.gitconnect.global.util.RestClientUtil;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubAPISerivce {

    private final ObjectMapper objectMapper;

    public List<FollowRespDto> getFollowers(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = getHeaders(accessToken);
        String result = RestClientUtil.get(GitHubUri.FOLLOWER_LIST.getUri(), headers);
        return getFollowersFromJson(result);
    }

    public List<FollowRespDto> getFollowing(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = getHeaders(accessToken);
        String result = RestClientUtil.get(GitHubUri.FOLLOWING_LIST.getUri(), headers);
        return getFollowersFromJson(result);
    }

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github+json");
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private List<FollowRespDto> getFollowersFromJson(String result) throws JsonProcessingException {
        try {
            List<FollowRespDto> users = new ArrayList<>();
            JsonNode arrayNode  = objectMapper.readTree(result);
            for (JsonNode node : arrayNode) {
                String login = node.path("login").asText();
                String avatarUrl = node.path("avatar_url").asText();
                String gravatarId = node.path("html_url").asText();
                users.add(new FollowRespDto(login, avatarUrl, gravatarId));
            }
            return users;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
