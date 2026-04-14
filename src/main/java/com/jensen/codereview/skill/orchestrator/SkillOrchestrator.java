/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SkillOrchestrator.java
 * @Package com.jensen.codereview.skill.orchestrator
 * @author: Jensen
 * @date:   2026/4/14 10:23
 * @version V1.0
 */
package com.jensen.codereview.skill.orchestrator;

import com.jensen.codereview.skill.base.Skill;
import com.jensen.codereview.skill.base.SkillContext;
import com.jensen.codereview.skill.base.SkillResult;
import com.jensen.codereview.skill.registry.SkillRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @ClassName SkillOrchestrator
 * @Description 技能编排
 * @Author Jensen
 * @Date 2026/4/14 10:23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SkillOrchestrator {

    /**
     * 技能注册中心
     */
    private final SkillRegistry skillRegistry;

    /**
     * 顺序执行技能
     * @param context 上下文
     * @return 技能结果列表
     */
    public List<SkillResult> executeSequential(SkillContext context) {
        List<SkillResult> results = new ArrayList<>();
        for (Skill skill : skillRegistry.getEnabledSkills()) {
            log.info("执行技能: {}", skill.getName());
            try {
                SkillResult result = skill.execute(context).join();
                results.add(result);
                context.putShared(skill.getName() + "_result", result);
            } catch (Exception e) {
                log.error("技能执行失败: {}", skill.getName(), e);
                results.add(SkillResult.builder()
                        .skillName(skill.getName())
                        .success(false)
                        .summary("执行失败: " + e.getMessage())
                        .issues(new ArrayList<>())
                        .suggestions(new ArrayList<>())
                        .build());
            }
        }
        return results;
    }

    /**
     * 并行执行技能
     * @param context 上下文
     * @return 技能结果列表
     */
    public CompletableFuture<List<SkillResult>> executeParallel(SkillContext context) {
        List<CompletableFuture<SkillResult>> futures = skillRegistry.getEnabledSkills().stream()
                .map(skill -> skill.execute(context))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /**
     * 合并技能结果
     * @param results 技能结果列表
     * @return 合并后的技能结果
     */
    public SkillResult mergeResults(List<SkillResult> results) {
        List<SkillResult.Issue> allIssues = new ArrayList<>();
        List<SkillResult.Suggestion> allSuggestions = new ArrayList<>();
        Map<String, Object> aggregatedMetrics = new HashMap<>();

        for (SkillResult result : results) {
            if (result.getIssues() != null) {
                allIssues.addAll(result.getIssues());
            }
            if (result.getSuggestions() != null) {
                allSuggestions.addAll(result.getSuggestions());
            }
            if (result.getMetrics() != null) {
                aggregatedMetrics.putAll(result.getMetrics());
            }
        }

        allIssues.sort((a, b) -> {
            String order = "CRITICAL,HIGH,MEDIUM,LOW";
            return Integer.compare(order.indexOf(a.getSeverity()), order.indexOf(b.getSeverity()));
        });

        return SkillResult.builder()
                .skillName("AggregatedResult")
                .success(true)
                .summary(generateSummary(allIssues))
                .issues(allIssues)
                .suggestions(allSuggestions)
                .metrics(aggregatedMetrics)
                .build();
    }

    /**
     * 生成技能结果摘要
     * @param issues 问题列表
     * @return 摘要
     */
    private String generateSummary(List<SkillResult.Issue> issues) {
        long critical = issues.stream().filter(i -> "CRITICAL".equals(i.getSeverity())).count();
        long high = issues.stream().filter(i -> "HIGH".equals(i.getSeverity())).count();

        if (critical > 0) {
            return String.format("🚨 发现 %d 个严重问题，必须立即修复！", critical);
        }
        if (high > 0) {
            return String.format("⚠️ 发现 %d 个高风险问题，建议优先处理", high);
        }
        if (issues.isEmpty()) {
            return "✅ 代码质量良好，未发现问题";
        }
        return String.format("📋 发现 %d 个问题，建议改进", issues.size());
    }
}
