package com.twoclock.gitconnect.domain.notification.web;

import com.twoclock.gitconnect.domain.notification.dto.NotificationRespDto;
import com.twoclock.gitconnect.domain.notification.service.NotificationService;
import com.twoclock.gitconnect.global.model.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping()
    public RestResponse getNotificationList(@AuthenticationPrincipal UserDetails userDetails) {
        String githubId = userDetails.getUsername();
        DeferredResult<List<NotificationRespDto>> result = notificationService.getNotificationList(githubId);
        return new RestResponse(result);
    }

}
