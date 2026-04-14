/*
 * All rights Reserved, Designed By Jensen
 * @Title:  HealthController.java
 * @Package com.jensen.codereview.controller
 * @author: Jensen
 * @date:   2026/4/14 11:10
 * @version V1.0
 */
package com.jensen.codereview.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName HealthController
 * @Description 健康检查控制器
 * @Author Jensen
 * @Date 2026/4/14 11:10
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * 健康检查
     * @return 健康状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "Code Review Agent",
                "version", "1.0.0"
        );
    }
}
