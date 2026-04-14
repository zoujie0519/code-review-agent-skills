/*
 * All rights Reserved, Designed By jensen
 * @Title:  CodeReviewApplication.java
 * @Package com.jensen.codereview
 * @author: Jensen
 * @date:   2026/4/14 10:10
 * @version V1.0
 */
package com.jensen.codereview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName CodeReviewApplication
 * @Description 项目启动文件
 * @Author Jensen
 * @Date 2026/4/14 10:11
 */

@Slf4j
@EnableScheduling
@SpringBootApplication
public class CodeReviewApplication {

    /**
     * 项目启动方法
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CodeReviewApplication.class, args);
    }

    /**
     * Web容器启动后获取实际端口打印地址，解决硬编码问题
     */
    @EventListener(WebServerInitializedEvent.class)
    public void afterStart(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        log.info("""
                ========================================
                Code Review Agent with Skills 已启动
                访问地址: http://localhost:{}/index.html
                ========================================
                """, port);
    }
}
