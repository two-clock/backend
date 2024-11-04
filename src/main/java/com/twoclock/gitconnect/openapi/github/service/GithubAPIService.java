package com.twoclock.gitconnect.openapi.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.domain.member.dto.MemberLoginRespDto;
import com.twoclock.gitconnect.global.util.RestClientUtil;
import com.twoclock.gitconnect.openapi.github.constants.GitHubUri;
import com.twoclock.gitconnect.openapi.github.dto.FollowRespDto;
import com.twoclock.gitconnect.openapi.github.dto.GitHubTokenDto;
import com.twoclock.gitconnect.openapi.github.dto.MemberGithubInfoDto;
import com.twoclock.gitconnect.openapi.github.dto.RepositoryRespDto;
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
    private static final String GITHUB_API_VERSION = "2022-11-28";

    private final ObjectMapper objectMapper;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    public MemberLoginRespDto getGitHubMember(String gitHubAccessToken) {
        HttpHeaders headers = createHeadersWithAccessToken(gitHubAccessToken);
        String result = RestClientUtil.get(GitHubUri.MY_GITHUB_INFO.getUri(), headers);
        return parseMemberLoginResponse(result);
    }

    public MemberGithubInfoDto getGitHubMemberInfo(String gitHubAccessToken, String githubName) {
        HttpHeaders headers = createHeadersWithAccessToken(gitHubAccessToken);
        String result = RestClientUtil.get(String.format(GitHubUri.GET_USER_INFO.getUri(), githubName), headers);
        return parseMemberInfoResponse(result);
    }

    public GitHubTokenDto getMemberGitHubToken(String code) {
        HttpHeaders headers = createDefaultHeaders();
        MultiValueMap<String, String> body = createTokenRequestBody(code);
        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);
        return parseGithubTokenDto(result);
    }

    public GitHubTokenDto refreshMemberGitHubToken(String gitHubRefreshToken) {
        HttpHeaders headers = createDefaultHeaders();
        MultiValueMap<String, String> body = createRefreshTokenRequestBody(gitHubRefreshToken);
        String result = RestClientUtil.post(GitHubUri.ACCESS_TOKEN.getUri(), headers, body);
        return parseGithubTokenDto(result);
    }

    public void deleteMemberGitHubToken(String gitHubAccessToken) {
        HttpHeaders headers = createHeadersWithBasicAuth();
        String body = "{ \"access_token\": \"" + gitHubAccessToken + "\" }";
        RestClientUtil.delete(String.format(GitHubUri.DELETE_TOKEN.getUri(), clientId), headers, body);
    }

    public List<FollowRespDto> getFollowers(String accessToken) {
        HttpHeaders headers = createHeadersWithAccessToken(accessToken);
        String result = RestClientUtil.get(GitHubUri.FOLLOWER_LIST.getUri(), headers);
        return parseFollowResponse(result);
    }

    public List<FollowRespDto> getFollowing(String accessToken) {
        HttpHeaders headers = createHeadersWithAccessToken(accessToken);
        String result = RestClientUtil.get(GitHubUri.FOLLOWING_LIST.getUri(), headers);
        return parseFollowResponse(result);
    }

    public List<RepositoryRespDto> getRepositories(String accessToken, String githubName) {
        HttpHeaders headers = createHeadersWithAccessToken(accessToken);
        String result = RestClientUtil.get(String.format(GitHubUri.MY_REPOSITORY_LIST.getUri(), githubName), headers);
        return parseRepositoryResponse(result);
    }

    private HttpHeaders createHeadersWithAccessToken(String accessToken) {
        HttpHeaders headers = createDefaultHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private HttpHeaders createHeadersWithBasicAuth() {
        HttpHeaders headers = createDefaultHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        return headers;
    }

    private HttpHeaders createDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", APPLICATION_VND_GITHUB_JSON);
        headers.add("X-GitHub-Api-Version", GITHUB_API_VERSION);
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_CHARSET_UTF8);
        return headers;
    }

    private MultiValueMap<String, String> createTokenRequestBody(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        return body;
    }

    private MultiValueMap<String, String> createRefreshTokenRequestBody(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        return body;
    }

    private MemberLoginRespDto parseMemberLoginResponse(String result) {
        try {
            JsonNode jsonNode = objectMapper.readTree(result);
            String login = jsonNode.get("login").asText();
            String gitHubId = jsonNode.get("id").asText();
            String avatarUrl = jsonNode.get("avatar_url").asText();
            String name = jsonNode.get("name").asText();
            return new MemberLoginRespDto(login, gitHubId, avatarUrl, name);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing GitHub member response", e);
        }
    }

    private MemberGithubInfoDto parseMemberInfoResponse(String result) {
        try {
            JsonNode jsonNode = objectMapper.readTree(result);
            String login = jsonNode.get("login").asText();
            String gitHubId = jsonNode.get("id").asText();
            String htmlUrl = jsonNode.get("html_url").asText();
            String avatarUrl = jsonNode.get("avatar_url").asText();
            String name = jsonNode.get("name").asText();
            String bio = jsonNode.get("bio").asText();
            String company = jsonNode.get("company").asText();
            String location = jsonNode.get("location").asText();
            String email = jsonNode.get("email").asText();
            String blog = jsonNode.get("blog").asText();
            int followers = Integer.parseInt(jsonNode.get("followers").asText());
            int following = Integer.parseInt(jsonNode.get("following").asText());
            int publicRepos = Integer.parseInt(jsonNode.get("public_repos").asText());
            int publicGists = Integer.parseInt(jsonNode.get("public_gists").asText());
            String createdAt = jsonNode.get("created_at").asText();
            String updatedAt = jsonNode.get("updated_at").asText();
            return new MemberGithubInfoDto(login, gitHubId, avatarUrl, name, bio, company, location, htmlUrl, email,
                    blog, followers, following, publicRepos, publicGists, createdAt, updatedAt);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing GitHub member response", e);
        }
    }


    private GitHubTokenDto parseGithubTokenDto(String result) {
        try {
            JsonNode jsonNode = objectMapper.readTree(result);
            String accessToken = jsonNode.get("access_token").asText();
            String refreshToken = jsonNode.get("refresh_token").asText();
            return new GitHubTokenDto(accessToken, refreshToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing GitHub token response", e);
        }
    }

    private List<FollowRespDto> parseFollowResponse(String result) {
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
            throw new RuntimeException("Error parsing followers/following response", e);
        }
    }

    private List<RepositoryRespDto> parseRepositoryResponse(String result) {
        try {
            List<RepositoryRespDto> repositories = new ArrayList<>();
            JsonNode arrayNode = objectMapper.readTree(result);

            arrayNode.forEach(node -> {
                String name = node.path("name").asText();
                String fullName = node.path("full_name").asText();
                String htmlUrl = node.path("html_url").asText();
                String description = node.path("description").asText();
                String createdAt = node.path("created_at").asText();
                String updatedAt = node.path("updated_at").asText();
                String pushedAt = node.path("pushed_at").asText();
                String size = node.path("size").asText();
                String stargazersCount = node.path("stargazers_count").asText();
                String watchersCount = node.path("watchers_count").asText();
                String language = node.path("language").asText();
                String forksCount = node.path("forks_count").asText();

                repositories.add(
                        new RepositoryRespDto(name, fullName, htmlUrl, description, createdAt,
                                updatedAt, pushedAt, size,stargazersCount, watchersCount,
                                language, forksCount)
                );
            });
            return repositories;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing repository response", e);
        }
    }
}

