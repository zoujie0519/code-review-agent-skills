/*
 * All rights Reserved, Designed By Jensen
 * @Title:  CodeReviewAgent.java
 * @Package com.jensen.codereview.agent
 * @author: Jensen
 * @date:   2026/4/14 10:31
 * @version V1.0
 */
package com.jensen.codereview.agent;

import com.jensen.codereview.skill.base.SkillContext;
import com.jensen.codereview.skill.base.SkillResult;
import com.jensen.codereview.skill.orchestrator.SkillOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CodeReviewAgent
 * @Description 代码审查代理
 * @Author Jensen
 * @Date 2026/4/14 10:32
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CodeReviewAgent {

    /**
     * 技能执行器
     */
    private final SkillOrchestrator orchestrator;

    /**
     * 聊天客户端
     */
    private final ChatClient chatClient;

    /**
     * 模型名称
     */
    @Value("${spring.ai.openai.chat.options.model:doubao-pro-32k}")
    private String modelName;

    /**
     * 代码审查
     * @param codeContent 代码内容
     * @return 审查结果
     */
    public String review(String codeContent) {
        log.info("开始代码审查");

        // 1. 创建上下文
        SkillContext context = SkillContext.create(codeContent);

        // 2. 执行技能链
        var results = orchestrator.executeSequential(context);
        SkillResult aggregated = orchestrator.mergeResults(results);

        // 3. AI 增强总结
        String aiSummary = generateAISummary(codeContent, aggregated);

        // 4. 生成最终报告
        return buildFinalReport(aggregated, aiSummary);
    }

    /**
     * AI 总结
     * @param code 源码
     * @param result 技能结果
     * @return 总结
     */
    private String generateAISummary(String code, SkillResult result) {
        try {
            // 限制代码长度，避免超出 token 限制
            String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
            
            String prompt = String.format("""
                你是一位拥有15年经验的资深Java架构师和技术专家，擅长代码审查、架构设计和性能优化。
                
                请基于以下【待审查代码】和【审查发现】，从架构师视角进行深度分析和专业评估：
                
                【待审查代码】
                ```java
                %s
                ```
                
                【审查发现汇总】
                %s
                
                【问题统计】
                - 问题总数：%d个
                - 严重问题：%d个
                - 高危问题：%d个
                - 中危问题：%d个
                - 低危问题：%d个
                
                请以专业的架构师视角，结合上述代码实际情况，提供以下维度的深度分析：
                
                ## 一、总体评价
                基于代码实际情况，从代码质量、架构设计、可维护性、可扩展性等维度给出综合评价（100分制）
                
                ## 二、核心问题分析
                结合代码中的具体问题，按优先级列出最关键的3-5个问题，每个问题包含：
                - 问题描述：详细说明问题的本质，引用代码中的具体位置和内容
                - 风险等级：CRITICAL/HIGH/MEDIUM/LOW
                - 业务影响：对系统稳定性、性能、安全性的具体影响
                - 技术债务：可能带来的长期维护成本
                
                ## 三、架构层面建议
                针对当前代码，从以下角度提供改进建议：
                1. 设计模式应用：是否合理使用了设计模式，有无过度设计或设计不足
                2. 代码结构：分层是否清晰，职责是否单一，耦合度是否合理
                3. 性能优化：是否存在性能瓶颈，如何优化（给出具体的代码改进示例）
                4. 安全性：是否存在安全隐患，如何加固（指出代码中的具体安全问题）
                5. 可维护性：代码可读性、可测试性、可扩展性如何提升
                
                ## 四、最佳实践推荐
                结合行业最佳实践，给出具体的改进方案和代码示例，展示如何重构这段代码
                
                ## 五、优先级行动计划
                给出分阶段的改进建议：
                - 立即修复（P0）：必须马上解决的问题，说明原因
                - 短期优化（P1）：1-2周内可以完成的优化
                - 中期改进（P2）：1-2个月内的改进计划
                - 长期规划（P3）：架构层面的长期优化方向
                
                要求：
                1. **必须紧密结合提供的代码进行分析**，引用代码中的具体内容
                2. 语言专业、严谨，体现架构师的深度思考
                3. 建议具体、可落地，避免空泛的理论
                4. 结合实际情况，权衡利弊，给出最优方案
                5. 使用技术术语要准确，必要时提供解释
                6. 重点突出，层次分明，便于团队理解和执行
                7. 对于关键问题，提供重构后的代码示例
                """,
                    codeSnippet,
                    result.getSummary(),
                    result.getIssues().size(),
                    result.getIssues().stream().filter(i -> "CRITICAL".equals(i.getSeverity())).count(),
                    result.getIssues().stream().filter(i -> "HIGH".equals(i.getSeverity())).count(),
                    result.getIssues().stream().filter(i -> "MEDIUM".equals(i.getSeverity())).count(),
                    result.getIssues().stream().filter(i -> "LOW".equals(i.getSeverity())).count()
            );

            ChatResponse response = chatClient.prompt()
                    .user(prompt)
                    .options(OpenAiChatOptions.builder()
                            .model(modelName)
                            .temperature(0.3)
                            .maxTokens(4000)
                            .build())
                    .call()
                    .chatResponse();

            return response != null ? response.getResult().getOutput().getText() : "AI总结生成失败";
        } catch (Exception e) {
            log.error("AI总结失败", e);
            return "基于规则审查：" + result.getSummary();
        }
    }

    /**
     * 生成最终报告
     * @param result 技能结果
     * @param aiSummary AI总结
     * @return 最终报告
     */
    private String buildFinalReport(SkillResult result, String aiSummary) {
        StringBuilder report = new StringBuilder();
        
        // 报告头部
        report.append("\n");
        report.append("╔" + "═".repeat(78) + "╗\n");
        report.append("║" + centerText("🎯 Java 代码架构审查报告", 78) + "║\n");
        report.append("╚" + "═".repeat(78) + "╝\n\n");
        
        // 审查概览
        report.append("┌" + "─".repeat(78) + "┐\n");
        report.append("│" + centerText("📊 审查概览", 78) + "│\n");
        report.append("└" + "─".repeat(78) + "┘\n\n");
        
        long criticalCount = result.getIssues().stream().filter(i -> "CRITICAL".equals(i.getSeverity())).count();
        long highCount = result.getIssues().stream().filter(i -> "HIGH".equals(i.getSeverity())).count();
        long mediumCount = result.getIssues().stream().filter(i -> "MEDIUM".equals(i.getSeverity())).count();
        long lowCount = result.getIssues().stream().filter(i -> "LOW".equals(i.getSeverity())).count();
        
        report.append(String.format("   🔴 严重问题 (CRITICAL): %d 个\n", criticalCount));
        report.append(String.format("   🟠 高危问题 (HIGH):     %d 个\n", highCount));
        report.append(String.format("   🟡 中危问题 (MEDIUM):   %d 个\n", mediumCount));
        report.append(String.format("   🔵 低危问题 (LOW):      %d 个\n", lowCount));
        report.append(String.format("   📈 问题总数:            %d 个\n", result.getIssues().size()));
        report.append(String.format("   ⏱️  审查耗时:            %d ms\n\n", result.getExecutionTimeMs()));
        
        // AI 深度分析
        report.append("┌" + "─".repeat(78) + "┐\n");
        report.append("│" + centerText("🧠 架构师深度分析", 78) + "│\n");
        report.append("└" + "─".repeat(78) + "┘\n\n");
        report.append(aiSummary).append("\n\n");
        
        // 智能问题分类汇总（替代原有的简单列表）
        if (!result.getIssues().isEmpty()) {
            report.append(buildSmartIssueSummary(result.getIssues()));
        }
        
        // 报告尾部
        report.append("╔" + "═".repeat(78) + "╗\n");
        report.append("║" + centerText("✅ 审查完成 | 专业 · 深入 · 可落地", 78) + "║\n");
        report.append("╚" + "═".repeat(78) + "╝\n");
        
        return report.toString();
    }
    
    /**
     * 构建智能问题分类汇总
     * @param issues 问题列表
     * @return 格式化后的问题汇总
     */
    private String buildSmartIssueSummary(List<SkillResult.Issue> issues) {
        StringBuilder summary = new StringBuilder();
        
        // 按严重程度分组
        Map<String, List<SkillResult.Issue>> groupedBySeverity = issues.stream()
                .collect(java.util.stream.Collectors.groupingBy(SkillResult.Issue::getSeverity));
        
        // 按类别分组
        Map<String, List<SkillResult.Issue>> groupedByCategory = issues.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        issue -> issue.getCategory() != null ? issue.getCategory() : "OTHER"
                ));
        
        summary.append("┌" + "─".repeat(78) + "┐\n");
        summary.append("│" + centerText("📋 问题分类汇总", 78) + "│\n");
        summary.append("└" + "─".repeat(78) + "┘\n\n");
        
        // 只显示关键问题（CRITICAL 和 HIGH）
        List<SkillResult.Issue> criticalIssues = groupedBySeverity.getOrDefault("CRITICAL", new ArrayList<>());
        List<SkillResult.Issue> highIssues = groupedBySeverity.getOrDefault("HIGH", new ArrayList<>());
        
        if (!criticalIssues.isEmpty() || !highIssues.isEmpty()) {
            summary.append("⚠️  **需要立即关注的关键问题**\n\n");
            
            int issueNum = 1;
            
            // 严重问题
            for (SkillResult.Issue issue : criticalIssues) {
                summary.append(String.format("%d. 🔴 [严重] %s\n", issueNum++, issue.getDescription()));
                if (issue.getLineNumber() != null && issue.getLineNumber() > 0) {
                    summary.append(String.format("   📍 位置: 第 %d 行\n", issue.getLineNumber()));
                }
                summary.append(String.format("   💡 建议: %s\n", issue.getFixSuggestion()));
                summary.append("\n");
            }
            
            // 高危问题
            for (SkillResult.Issue issue : highIssues) {
                summary.append(String.format("%d. 🟠 [高危] %s\n", issueNum++, issue.getDescription()));
                if (issue.getLineNumber() != null && issue.getLineNumber() > 0) {
                    summary.append(String.format("   📍 位置: 第 %d 行\n", issue.getLineNumber()));
                }
                summary.append(String.format("   💡 建议: %s\n", issue.getFixSuggestion()));
                summary.append("\n");
            }
        }
        
        // 按类别统计（中危和低危问题只展示统计信息）
        List<SkillResult.Issue> mediumAndLow = issues.stream()
                .filter(i -> "MEDIUM".equals(i.getSeverity()) || "LOW".equals(i.getSeverity()))
                .toList();
        
        if (!mediumAndLow.isEmpty()) {
            summary.append("\n📊 **其他问题统计**（共 " + mediumAndLow.size() + " 个，建议逐步优化）\n\n");
            
            groupedByCategory.forEach((category, categoryIssues) -> {
                long count = categoryIssues.stream()
                        .filter(i -> "MEDIUM".equals(i.getSeverity()) || "LOW".equals(i.getSeverity()))
                        .count();
                if (count > 0) {
                    String categoryName = getCategoryDisplayName(category);
                    summary.append(String.format("   • %s: %d 个问题\n", categoryName, count));
                }
            });
            
            summary.append("\n💡 提示: 以上问题已在「架构师深度分析」中给出综合改进建议\n");
        }
        
        summary.append("\n" + "   " + "-".repeat(76) + "\n\n");
        
        return summary.toString();
    }
    
    /**
     * 获取类别显示名称
     * @param category 类别代码
     * @return 显示名称
     */
    private String getCategoryDisplayName(String category) {
        return switch (category) {
            case "STYLE" -> "代码风格";
            case "SECURITY" -> "安全隐患";
            case "BUG" -> "潜在Bug";
            case "PERFORMANCE" -> "性能问题";
            case "BEST_PRACTICE" -> "最佳实践";
            case "COMPLEXITY" -> "复杂度";
            case "DOCUMENTATION" -> "文档注释";
            case "QUALITY" -> "代码质量";
            default -> "其他问题";
        };
    }
    
    /**
     * 文本居中
     * @param text 文本
     * @param width 宽度
     * @return 居中后的文本
     */
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - padding - text.length()));
    }
}
