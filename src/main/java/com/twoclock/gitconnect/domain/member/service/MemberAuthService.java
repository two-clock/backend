package com.twoclock.gitconnect.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoclock.gitconnect.domain.member.dto.MemberInfoDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.util.RestClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
@Service
public class MemberAuthService {

    private final MemberRepository memberRepository;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    public void githubLogin(String code) {
        String gitHubAccessToken = getGitHubAccessToken(code);
        MemberInfoDto memberInfoDto = getGitHubMemberInfo(gitHubAccessToken);
        registerOrUpdateMember(memberInfoDto);
    }

    private String getGitHubAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        String result =
                RestClientUtil.post("https://github.com/login/oauth/access_token?scope=user", headers, body);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private MemberInfoDto getGitHubMemberInfo(String gitHubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + gitHubAccessToken);
        headers.add("Accept", "application/vnd.github+json");

        String result = RestClientUtil.get("https://api.github.com/user", headers);

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(result);
            String login = jsonNode.get("login").asText();
            String avatarUrl = jsonNode.get("avatar_url").asText();
            String name = jsonNode.get("name").asText();
            return new MemberInfoDto(login, avatarUrl, name);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerOrUpdateMember(MemberInfoDto memberInfoDto) {
        String token = memberInfoDto.login();
        String avatarUrl = memberInfoDto.avatarUrl();
        String name = memberInfoDto.name();

        memberRepository.findByLogin(token).ifPresentOrElse(
                (member) -> member.update(token, avatarUrl, name),
                () -> {
                    Member member = Member.builder()
                            .login(token)
                            .avatarUrl(avatarUrl)
                            .name(name)
                            .role(Role.ROLE_USER)
                            .build();

                    memberRepository.save(member);
                }
        );
    }
}
