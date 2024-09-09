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
    private final Long NOTIFY_TIME_OUT = 60000L;

    @Transactional(readOnly = true)
    public DeferredResult<List<NotificationRespDto>> getNotificationList(String githubId) {
        DeferredResult<List<NotificationRespDto>> deferredResult = new DeferredResult<>(NOTIFY_TIME_OUT);

        Member member = validateMember(githubId);
        List<Notification> notifications = notificationRepository.findByMemberAndIsReadFalse(member);
        if (!notifications.isEmpty()) {
            // 새로운 알림이 있으면 바로 응답
            deferredResult.setResult(toMapNotificationResp(notifications));
        } else {
            // 새로운 알림이 없으면 대기 목록에 추가
            waitingUsers.computeIfAbsent(member, k -> new ArrayList<>()).add(deferredResult);
        }

        deferredResult.onTimeout(() -> {
            // 빈 List 반환
            deferredResult.setResult(new ArrayList<>());
            removeWaitingUser(member, deferredResult);
        });

        // 요청이 완료되면 대기 목록에서 제거
        deferredResult.onCompletion(() -> removeWaitingUser(member, deferredResult));

        return deferredResult;
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
            results.remove(deferredResult);
            if (results.isEmpty()) {
                waitingUsers.remove(member);
            }
        }
    }
}
