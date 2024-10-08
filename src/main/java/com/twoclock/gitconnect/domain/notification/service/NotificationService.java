package com.twoclock.gitconnect.domain.notification.service;

import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.domain.notification.dto.NotificationRespDto;
import com.twoclock.gitconnect.domain.notification.entity.Notification;
import com.twoclock.gitconnect.domain.notification.entity.constants.NotificationType;
import com.twoclock.gitconnect.domain.notification.repository.NotificationRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    private final Map<Member, List<DeferredResult<List<NotificationRespDto>>>> waitingUsers = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public List<NotificationRespDto> getNotificationInfo(String githubId) {
        Member member = validateMember(githubId);
        List<Notification> notifications = notificationRepository.findByMemberOrderByCreatedDateTimeDesc(member);
        return toMapNotificationResp(notifications);
    }

    @Transactional
    public void readNotification(Long notificationId, String githubId) {
        Member member = validateMember(githubId);
        Notification notification = validateNotification(notificationId, member);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long notificationId, String githubId) {
        Member member = validateMember(githubId);
        Notification notification = validateNotification(notificationId, member);
        notificationRepository.delete(notification);
    }

    @Transactional(readOnly = true)
    public DeferredResult<List<NotificationRespDto>> getNotificationList(String githubId) {
        DeferredResult<List<NotificationRespDto>> deferredResult = new DeferredResult<>(60000L);

        Member member = validateMember(githubId);
        List<Notification> notifications = notificationRepository.findByMemberAndIsSentFalseOrderByCreatedDateTimeDesc(member);

        if (!notifications.isEmpty()) {
            deferredResult.setResult(toMapNotificationResp(notifications));
        } else {
            waitingUsers.computeIfAbsent(member, k -> new ArrayList<>()).add(deferredResult);
        }

        deferredResult.onTimeout(() -> {
            deferredResult.setResult(new ArrayList<>());
            removeWaitingUser(member, deferredResult);
        });

        deferredResult.onCompletion(() -> removeWaitingUser(member, deferredResult));

        return deferredResult;
    }

    @Transactional
    public void addNotificationInfo(Member member, NotificationType type, String userId) {
        Notification notification = Notification.builder()
                .member(member)
                .type(type)
                .message(userId + type.getMessage())
                .build();
        notificationRepository.save(notification);
        notifyUser(member);
    }

    private void notifyUser(Member member) {
        List<Notification> notifications = notificationRepository.findByMemberAndIsSentFalseOrderByCreatedDateTimeDesc(member);
        if (!notifications.isEmpty()) {
            notifications.forEach(n -> n.setSent(true));
            notificationRepository.saveAll(notifications);
        }

        List<DeferredResult<List<NotificationRespDto>>> userDeferredResults = getAllDeferredResults();
        if (!userDeferredResults.isEmpty()) {
            userDeferredResults.forEach(r -> {
                r.setResult(toMapNotificationResp(notifications));
            });
        }
    }

    private List<DeferredResult<List<NotificationRespDto>>> getAllDeferredResults() {
        List<DeferredResult<List<NotificationRespDto>>> allDeferredResults = new ArrayList<>();
        for (List<DeferredResult<List<NotificationRespDto>>> deferredResults : waitingUsers.values()) {
            allDeferredResults.addAll(deferredResults);
        }
        return allDeferredResults;
    }

    private Member validateMember(String githubId) {
        return memberRepository.findByGitHubId(githubId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private Notification validateNotification(Long notificationId, Member member) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));
        if (!notification.getMember().equals(member)) {
            throw new CustomException(ErrorCode.NOT_MATCH_MEMBER);
        }
        return notification;
    }

    private List<NotificationRespDto> toMapNotificationResp(List<Notification> notifications) {
        return notifications.stream().map(NotificationRespDto::new).toList();
    }

    private void removeWaitingUser(Member member, DeferredResult<List<NotificationRespDto>> deferredResult) {
        List<DeferredResult<List<NotificationRespDto>>> results = waitingUsers.get(member);
        if (results != null) {
            results.remove(deferredResult);
            if (results.isEmpty()) {
                waitingUsers.remove(member);
            }
        }
    }
}
