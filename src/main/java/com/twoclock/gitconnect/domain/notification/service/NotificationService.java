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
    public DeferredResult<List<NotificationRespDto>> getNotificationList(String githubId) {
        DeferredResult<List<NotificationRespDto>> deferredResult = new DeferredResult<>(60000L);

        Member member = validateMember(githubId);
        List<Notification> notifications = notificationRepository.findByMemberAndIsSentFalseOrderByCreatedDateTimeDesc(member);
        System.out.println("알림 목록 조회: " + notifications.size());

        if (!notifications.isEmpty()) {
            System.out.println("새로운 알림이 있으면 바로 응답");
            deferredResult.setResult(toMapNotificationResp(notifications));
        } else {
            System.out.println("새로운 알림이 없으면 대기 목록에 추가");
            waitingUsers.computeIfAbsent(member, k -> new ArrayList<>()).add(deferredResult);
        }

        deferredResult.onTimeout(() -> {
            System.out.println("타임 아웃으로 인한 빈 알람 반환");
            deferredResult.setResult(new ArrayList<>());
            removeWaitingUser(member, deferredResult);
        });

        System.out.println("요청이 완료되면 대기 목록에서 제거");
        deferredResult.onCompletion(() -> removeWaitingUser(member, deferredResult));

        return deferredResult;
    }

    @Transactional
    public void addNotificationInfo(Member member, NotificationType type) {
        Notification notification = Notification.builder()
                .member(member)
                .type(type)
                .message(member.getLogin() + type.getMessage())
                .build();
        notificationRepository.save(notification);
        notifyUser(member);
    }

    private void notifyUser(Member member) {
        List<Notification> notifications = notificationRepository.findByMemberAndIsSentFalseOrderByCreatedDateTimeDesc(member);
        if (!notifications.isEmpty()) {
            System.out.println("알림 전송 후 알림 상태 변경");
            notifications.forEach(n -> n.setSent(true));
            notificationRepository.saveAll(notifications);
        }

        List<DeferredResult<List<NotificationRespDto>>> userDeferredResults = getAllDeferredResults();
        if (!userDeferredResults.isEmpty()) {
            userDeferredResults.forEach(r -> {
                System.out.println("대기중인 알림 객체에게 알림 전송");
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

    private List<NotificationRespDto> toMapNotificationResp(List<Notification> notifications) {
        return notifications.stream().map(NotificationRespDto::new).toList();
    }

    private void removeWaitingUser(Member member, DeferredResult<List<NotificationRespDto>> deferredResult) {
        List<DeferredResult<List<NotificationRespDto>>> results = waitingUsers.get(member);
        if (results != null) {
            System.out.println("대기 목록에서 제거 시도: " + member.getLogin());
            results.remove(deferredResult);
            if (results.isEmpty()) {
                waitingUsers.remove(member);
                System.out.println("대기 목록에서 사용자 제거됨: " + member.getLogin());
            }
        }
    }
}
