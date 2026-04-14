/*
 * All rights Reserved, Designed By Jensen
 * @Title:  MetricsCollector.java
 * @Package com.jensen.codereview.utils
 * @author: Jensen
 * @date:   2026/4/14 11:08
 * @version V1.0
 */
package com.jensen.codereview.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName MetricsCollector
 * @Description 指标收集器
 * @Author Jensen
 * @Date 2026/4/14 11:08
 */
@Slf4j
@Data
public class MetricsCollector {

    private final Map<String, Long> counters = new ConcurrentHashMap<>();
    private final Map<String, Long> timers = new ConcurrentHashMap<>();

    /**
     * 增加计数
     * @param name 指标名称
     */
    public void increment(String name) {
        counters.merge(name, 1L, Long::sum);
    }

    /**
     * 记录时间
     * @param name 指标名称
     * @param durationMs 耗时(毫秒)
     */
    public void recordTime(String name, long durationMs) {
        timers.put(name, durationMs);
    }

    /**
     * 获取所有指标
     * @return 指标数据
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        metrics.put("counters", counters);
        metrics.put("timers", timers);
        return metrics;
    }

    /**
     * 重置所有指标
     */
    public void reset() {
        counters.clear();
        timers.clear();
    }
}
