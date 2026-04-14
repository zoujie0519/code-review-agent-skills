/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SkillResult.java
 * @Package com.jensen.codereview.skill.base
 * @author: Jensen
 * @date:   2026/4/14 10:19
 * @version V1.0
 */
package com.jensen.codereview.skill.base;

import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SkillResult
 * @Description 技能执行结果
 * @Author Jensen
 * @Date 2026/4/14 10:19
 */
@Data
@Builder
public class SkillResult {

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 技能执行结果摘要
     */
    private String summary;

    /**
     * 技能执行结果详情
     */
    private List<Issue> issues;

    /**
     * 技能执行建议
     */
    private List<Suggestion> suggestions;

    /**
     * 技能执行时间
     */
    private Long executionTimeMs;

    /**
     * 技能执行指标
     */
    private Map<String, Object> metrics;

    /**
     * 创建一个空的技能执行结果
     * @param skillName 技能名称
     * @return 技能执行结果
     */
    public static SkillResult empty(String skillName) {
        return SkillResult.builder()
                .skillName(skillName)
                .success(true)
                .issues(new ArrayList<>())
                .suggestions(new ArrayList<>())
                .metrics(new HashMap<>())
                .build();
    }

    /**
     * 技能执行结果详情
     */
    @Data
    @Builder
    public static class Issue {
        private String severity;
        private String category;
        private Integer lineNumber;
        private String description;
        private String codeSnippet;
        private String fixSuggestion;
    }

    /**
     * 技能执行建议
     */
    @Data
    @Builder
    public static class Suggestion {
        private String title;
        private String description;
        private String example;
    }
}
