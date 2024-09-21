package com.twoclock.gitconnect.domain.notification.dto;

import com.twoclock.gitconnect.domain.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationRespDto(
        Long id,
        String message,
        LocalDateTime createdDateTime,
        boolean isRead
) {
    public NotificationRespDto(Notification notification) {
        this(
                notification.getId(),
                notification.getMessage(),
                notification.getCreatedDateTime(),
                notification.isRead()
        );
    }
}
