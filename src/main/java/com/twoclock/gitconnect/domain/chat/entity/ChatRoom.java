package com.twoclock.gitconnect.domain.chat.entity;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE chat_room SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_member_id", nullable = false)
    private Member createdMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_member_id", nullable = false)
    private Member receivedMember;

    @Column(nullable = false)
    private final Boolean isDeleted = Boolean.FALSE;

    @Builder
    public ChatRoom(String chatRoomId, Member createdMember, Member receivedMember) {
        this.chatRoomId = chatRoomId;
        this.createdMember = createdMember;
        this.receivedMember = receivedMember;
    }
}
