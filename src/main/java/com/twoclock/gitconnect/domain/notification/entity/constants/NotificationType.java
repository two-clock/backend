package com.twoclock.gitconnect.domain.notification.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    COMMENT("댓글"),
    LIKES("좋아요"),
    CHAT("채팅");

    private final String description;
}
