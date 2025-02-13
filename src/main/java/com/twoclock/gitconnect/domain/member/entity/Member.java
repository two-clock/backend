package com.twoclock.gitconnect.domain.member.entity;

import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false, unique = true)
    private String gitHubId;

    @Column(nullable = false)
    private String avatarUrl;

    @Column
    private String name;

    @Column(nullable = false)
    private boolean isAgree = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String login, String gitHubId, String avatarUrl, String name, Role role) {
        this.login = login;
        this.gitHubId = gitHubId;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.role = role;
    }

    public void update(String login, String avatarUrl, String name) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.name = name;
    }

    public void agree() {
        this.isAgree = true;
    }
}
