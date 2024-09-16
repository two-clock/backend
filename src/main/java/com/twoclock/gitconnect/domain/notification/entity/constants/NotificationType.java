package com.twoclock.gitconnect.domain.notification.entity.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    COMMENT("댓글", "님이 댓글을 남겼습니다."),
    LIKES("좋아요", "님이 좋아요를 눌렀습니다."),
    CHAT("채팅", "님이 채팅을 보냈습니다.");

    private final String description;
    private final String message;
}
