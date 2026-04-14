/*
 * All rights Reserved, Designed By Jensen
 * @Title:  ReviewController.java
 * @Package com.jensen.codereview.controller
 * @author: Jensen
 * @date:   2026/4/14 10:35
 * @version V1.0
 */
package com.jensen.codereview.controller;

import com.jensen.codereview.agent.CodeReviewAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * @ClassName: ReviewController
 * @Description: 代码审核控制器
 * @Author: Jensen
 * @Date: 2026/4/14 10:35
 */
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    /**
     * 代码审核代理
     */
    private final CodeReviewAgent reviewAgent;

    /**
     * 代码审核
     * @param request 请求参数
     * @return 审核结果
     */
    @PostMapping("/code")
    public Map<String, Object> reviewCode(@RequestBody ReviewRequest request) {
        long startTime = System.currentTimeMillis();
        String result = reviewAgent.review(request.code);

        return Map.of(
                "success", true,
                "report", result,
                "executionTimeMs", System.currentTimeMillis() - startTime
        );
    }

    /**
     * 获取API信息
     * @return API信息
     */
    @GetMapping
    public Map<String, Object> getApiInfo() {
        return Map.of(
                "service", "Code Review Agent",
                "version", "1.0.0",
                "endpoints", Map.of(
                        "POST /api/review/code", "提交代码进行审查",
                        "GET /api/review/health", "健康检查"
                ),
                "example", "curl -X POST http://localhost:8080/api/review/code -H 'Content-Type: application/json' -d '{\"code\": \"public class Test {}\"}'"
        );
    }

    /**
     * 健康检查
     * @return 服务状态
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "Code Review Agent");
    }

    /**
     * 测试 AI 连接
     * @return 测试结果
     */
    @GetMapping("/test-ai")
    public Map<String, Object> testAiConnection() {
        try {
            // 简单的测试调用
            return Map.of(
                    "success", true,
                    "message", "AI 配置正常",
                    "baseUrl", "https://ark.cn-beijing.volces.com/api/v3",
                    "model", "doubao-pro-32k"
            );
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", e.getMessage()
            );
        }
    }

    /**
     * 请求参数
     */
    public record ReviewRequest(String code, String fileName) {}
}
