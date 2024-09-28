package com.twoclock.gitconnect.domain.notification.entity;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.notification.entity.constants.NotificationType;
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
@SQLDelete(sql = "UPDATE notification SET is_delete = true WHERE id = ?")
@Where(clause = "is_delete = false")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private boolean isSent = false;

    @Column(nullable = false)
    private final boolean isDelete = false;

    @Builder
    public Notification(Member member, NotificationType type, String message) {
        this.member = member;
        this.type = type;
        this.message = message;
    }

    public void setSent(boolean isSent) {
        this.isSent = isSent;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
