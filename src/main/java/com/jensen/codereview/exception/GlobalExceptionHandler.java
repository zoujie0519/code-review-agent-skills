/*
 * All rights Reserved, Designed By Jensen
 * @Title:  GlobalExceptionHandler.java
 * @Package com.jensen.codereview.exception
 * @author: Jensen
 * @date:   2026/4/14 11:02
 * @version V1.0
 */
package com.jensen.codereview.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * @ClassName GlobalExceptionHandler
 * @Description 全局异常处理器
 * @Author Jensen
 * @Date 2026/4/14 11:02
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理代码审查异常
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<Map<String, Object>> handleReviewException(ReviewException e) {
        log.error("代码审查异常", e);
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
        ));
    }

    /**
     * 处理技能执行异常
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(SkillExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleSkillExecutionException(SkillExecutionException e) {
        log.error("技能执行异常", e);
        return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
        ));
    }

    /**
     * 处理其他异常
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "系统内部错误"
        ));
    }
}
