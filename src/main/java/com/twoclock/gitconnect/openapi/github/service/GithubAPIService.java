package com.twoclock.gitconnect.openapi.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import com.twoclock.gitconnect.global.util.RestClientUtil;
import com.twoclock.gitconnect.openapi.github.constants.GitHubUri;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
import com.twoclock.gitconnect.openapi.github.dto.GitHubTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GithubAPIService {

    private static final String APPLICATION_FORM_URLENCODED_CHARSET_UTF8 = "application/x-www-form-urlencoded;charset=utf-8";
    private static final String APPLICATION_VND_GITHUB_JSON = "application/vnd.github+json";

    private final ObjectMapper objectMapper;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    public MemberLoginRespDto getGitHubMember(String gitHubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Authorization", "Bearer " + gitHubAccessToken);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);

        String result = RestClientUtil.get(GitHubUri.USER_INFO.getUri(), headers);
        return parseMemberLoginResponse(result);
    }

    public GitHubTokenDto getMemberGitHubToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);
        return parseGithubTokenDto(result);
    }

    public GitHubTokenDto refreshMemberGitHubToken(String gitHubRefreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", gitHubRefreshToken);

        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);
        return parseGithubTokenDto(result);
    }

    public void deleteMemberGitHubToken(String gitHubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);
        headers.setBasicAuth(clientId, clientSecret);

        String body = "{ \"access_token\": \"" + gitHubAccessToken + "\" }";

        RestClientUtil.delete(String.format(GitHubUri.DELETE_TOKEN.getUri(), clientId), headers, body);
    }

    public List<FollowRespDto> getFollowers(String accessToken) {
        HttpHeaders headers = getHeaders(accessToken);
        String result = RestClientUtil.get(GitHubUri.FOLLOWER_LIST.getUri(), headers);
        return getFollowersFromJson(result);
    }

    public List<FollowRespDto> getFollowing(String accessToken) {
        HttpHeaders headers = getHeaders(accessToken);
        String result = RestClientUtil.get(GitHubUri.FOLLOWING_LIST.getUri(), headers);
        return getFollowersFromJson(result);
    }

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private List<FollowRespDto> getFollowersFromJson(String result) {
        try {
            List<FollowRespDto> users = new ArrayList<>();
            JsonNode arrayNode = objectMapper.readTree(result);
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

    private MemberLoginRespDto parseMemberLoginResponse(String result) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String login = jsonNode.get("login").asText();
            String gitHubId = jsonNode.get("id").asText();
            String avatarUrl = jsonNode.get("avatar_url").asText();
            String name = jsonNode.get("name").asText();
            return new MemberLoginRespDto(login, gitHubId, avatarUrl, name);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private GitHubTokenDto parseGithubTokenDto(String result) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();
            return new GitHubTokenDto(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
