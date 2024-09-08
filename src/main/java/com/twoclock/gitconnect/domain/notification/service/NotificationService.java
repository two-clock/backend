package com.twoclock.gitconnect.domain.notification.service;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.domain.notification.dto.NotificationRespDto;
import com.twoclock.gitconnect.domain.notification.entity.Notification;
import com.twoclock.gitconnect.domain.notification.repository.NotificationRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<NotificationRespDto> getNotificationList(String githubId) {
        Member member = validateMember(githubId);
        List<Notification> notifications = notificationRepository.findByMemberAndIsReadFalse(member);
        return notifications.stream().map(NotificationRespDto::new).toList();
    }

    private Member validateMember(String githubId) {
        return memberRepository.findByGitHubId(githubId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
