package com.twoclock.gitconnect.domain.notification.web;

import com.twoclock.gitconnect.domain.notification.dto.NotificationRespDto;
import com.twoclock.gitconnect.domain.notification.service.NotificationService;
import com.twoclock.gitconnect.global.model.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    @GetMapping("/list")
    public RestResponse getNotificationInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        List<NotificationRespDto> result = notificationService.getNotificationInfo(githubId);
        return new RestResponse(result);
    }

    @PutMapping("{notificationId}")
    public RestResponse readNotification(@PathVariable Long notificationId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        notificationService.readNotification(notificationId, githubId);
        return RestResponse.OK();
    }

    @DeleteMapping("{notificationId}")
    public RestResponse deleteNotification(@PathVariable Long notificationId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        notificationService.deleteNotification(notificationId, githubId);
        return RestResponse.OK();
    }

    @GetMapping
    public DeferredResult<List<NotificationRespDto>> getNotificationList(@AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        return notificationService.getNotificationList(githubId);
    }
}
