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
    private String token;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String token, String profileImageUrl, String name, Role role) {
        this.token = token;
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.role = role;
    }

    public void update(String token, String profileImageUrl, String name) {
        this.token = token;
        this.profileImageUrl = profileImageUrl;
        this.name = name;
    }
}
