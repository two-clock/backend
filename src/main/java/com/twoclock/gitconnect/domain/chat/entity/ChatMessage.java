package com.twoclock.gitconnect.domain.chat.entity;

import com.twoclock.gitconnect.domain.member.entity.Member;
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
    private Member senderMember;
    private String message;
    private LocalDateTime createdDateTime;

    @Builder
    public ChatMessage(String chatRoomId, Member senderMember, String message, LocalDateTime createdDateTime) {
        this.chatRoomId = chatRoomId;
        this.senderMember = senderMember;
        this.message = message;
        this.createdDateTime = createdDateTime;
    }
}
