/*
 * All rights Reserved, Designed By Jensen
 * @Title:  BaseAISkill.java
 * @Package com.jensen.codereview.skill.base
 * @author: Jensen
 * @date:   2026/4/14
 * @version V1.0
 */
package com.jensen.codereview.skill.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName BaseAISkill
 * @Description AI 技能基类，提供通用的 AI 调用能力
 * @Author Jensen
 * @Date 2026/4/14
 */
@Slf4j
public abstract class BaseAISkill implements Skill {

    protected final ChatClient chatClient;

    @Value("${spring.ai.openai.chat.options.model:ark-code-latest}")
    protected String modelName;

    public BaseAISkill(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 获取 AI 分析提示词
     * @param code 代码内容
     * @return 提示词
     */
    protected abstract String getAIPrompt(String code);

    /**
     * 获取问题类别
     * @return 类别
     */
    protected abstract String getCategory();

    /**
     * 执行 AI 分析
     * @param code 代码内容
     * @return 问题列表
     */
    protected List<SkillResult.Issue> analyzeWithAI(String code) {
        try {
            String prompt = getAIPrompt(code);
            
            String response = chatClient.prompt()
                    .user(prompt)
                    .options(OpenAiChatOptions.builder()
                            .model(modelName)
                            .temperature(0.2)
                            .maxTokens(3000)
                            .build())
                    .call()
                    .content();

            return parseAIResponse(response);
        } catch (Exception e) {
            log.error("AI 分析失败: {}", getName(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 解析 AI 返回的 JSON
     * @param jsonResponse JSON 响应
     * @return 问题列表
     */
    protected List<SkillResult.Issue> parseAIResponse(String jsonResponse) {
        List<SkillResult.Issue> issues = new ArrayList<>();
        
        // 匹配 JSON 数组中的对象
        Pattern pattern = Pattern.compile(
                "\\{\\s*\"severity\"\\s*:\\s*\"(\\w+)\"\\s*,\\s*" +
                "\"lineNumber\"\\s*:\\s*(\\d+)\\s*,\\s*" +
                "\"description\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*" +
                "\"fixSuggestion\"\\s*:\\s*\"([^\"]+)\""
        );
        
        Matcher matcher = pattern.matcher(jsonResponse);
        while (matcher.find()) {
            issues.add(SkillResult.Issue.builder()
                    .severity(matcher.group(1))
                    .category(getCategory())
                    .lineNumber(Integer.parseInt(matcher.group(2)))
                    .description(matcher.group(3))
                    .fixSuggestion(matcher.group(4))
                    .build());
        }
        
        return issues;
    }

    /**
     * 构建执行结果
     * @param issues 问题列表
     * @param startTime 开始时间
     * @return 执行结果
     */
    protected SkillResult buildResult(List<SkillResult.Issue> issues, long startTime) {
        return SkillResult.builder()
                .skillName(getName())
                .success(true)
                .summary(String.format("发现 %d 个%s问题", issues.size(), getCategoryDisplayName()))
                .issues(issues)
                .suggestions(new ArrayList<>())
                .metrics(Map.of("totalIssues", issues.size()))
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 获取类别显示名称
     * @return 显示名称
     */
    protected String getCategoryDisplayName() {
        return switch (getCategory()) {
            case "STYLE" -> "代码风格";
            case "SECURITY" -> "安全隐患";
            case "BUG" -> "潜在Bug";
            case "PERFORMANCE" -> "性能";
            case "BEST_PRACTICE" -> "最佳实践";
            case "COMPLEXITY" -> "复杂度";
            case "DOCUMENTATION" -> "文档注释";
            case "QUALITY" -> "代码质量";
            default -> "";
        };
    }
}
