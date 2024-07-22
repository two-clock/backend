package com.twoclock.gitconnect.global.slack.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Profile(value = "dev")
@Aspect
@Component
public class SlackNotificationAspect {

    private final SlackApi slackApi;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Around(
            value = "@annotation(com.twoclock.gitconnect.global.slack.annotation.SlackNotification) && args(request, e)",
            argNames = "proceedingJoinPoint,request,e"
    )
    public Object slackNotification(
            ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request, Exception e
    ) throws Throwable {
        threadPoolTaskExecutor.execute(() -> sendSlackErrorMessage(request, e));

        return proceedingJoinPoint.proceed();
    }

    private void sendSlackErrorMessage(HttpServletRequest request, Exception e) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        SlackAttachment slackAttachment = new SlackAttachment();
        slackAttachment.setFallback("Error");
        slackAttachment.setColor("danger");
        slackAttachment.setTitle("Error Log");
        slackAttachment.setTitleLink(request.getContextPath());
        slackAttachment.setText(Arrays.toString(e.getStackTrace()));
        slackAttachment.setFields(
                Arrays.asList(
                        new SlackField().setTitle("Request URL").setValue(request.getRequestURL().toString()),
                        new SlackField().setTitle("Request Method").setValue(request.getMethod()),
                        new SlackField().setTitle("Request Time").setValue(now),
                        new SlackField().setTitle("Request IP").setValue(request.getRemoteAddr()),
                        new SlackField().setTitle("Request User-Agent").setValue(request.getHeader("User-Agent"))
                )
        );

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackMessage.setIcon(":x:");
        slackMessage.setText("에러가 발생했습니다.");
        slackMessage.setUsername("에러 봇");
        slackApi.call(slackMessage);
    }
}
