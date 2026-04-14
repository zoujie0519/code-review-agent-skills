/*
 * All rights Reserved, Designed By Jensen
 * @Title:  ThreadPoolConfig.java
 * @Package com.jensen.codereview.config
 * @author: Jensen
 * @date:   2026/4/14 10:45
 * @version V1.0
 */
package com.jensen.codereview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadPoolConfig
 * @Description 线程池配置
 * @Author Jensen
 * @Date 2026/4/14 10:45
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 创建代码审查线程池
     * @return Executor
     */
    @Bean("codeReviewExecutor")
    public Executor codeReviewExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("code-review-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
