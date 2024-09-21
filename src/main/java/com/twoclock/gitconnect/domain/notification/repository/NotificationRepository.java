package com.twoclock.gitconnect.domain.notification.repository;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberAndIsSentFalseOrderByCreatedDateTimeDesc(Member member);

    List<Notification> findByMemberOrderByCreatedDateTimeDesc(Member member);
}
