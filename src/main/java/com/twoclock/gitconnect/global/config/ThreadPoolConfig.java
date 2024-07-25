package com.twoclock.gitconnect.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    private static final int MAX_POOL_SIZE = 5; // 최대 스레드 수
    private static final int CORE_POOL_SIZE = 5; // 기본 스레드 수

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
