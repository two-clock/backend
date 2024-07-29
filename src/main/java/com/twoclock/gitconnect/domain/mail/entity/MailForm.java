package com.twoclock.gitconnect.domain.mail.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MailForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
