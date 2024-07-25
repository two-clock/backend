package com.twoclock.gitconnect.global.config;

import net.gpedro.integrations.slack.SlackApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackLogAppenderConfig {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Bean
    public SlackApi slackApi() {
        return new SlackApi(webhookUrl);
    }
}
