package com.twoclock.gitconnect.domain.chat.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;
    private String chatRoomId;
    private String senderGitHubId;
    private String message;
    private LocalDateTime createdDateTime;

    @Builder
    public ChatMessage(String chatRoomId, String senderGitHubId, String message, LocalDateTime createdDateTime) {
        this.chatRoomId = chatRoomId;
        this.senderGitHubId = senderGitHubId;
        this.message = message;
        this.createdDateTime = createdDateTime;
    }
}
