/*
 * All rights Reserved, Designed By Jensen
 * @Title:  ComplexitySkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:54
 * @version V1.0
 */
package com.jensen.codereview.skill.impl;

import com.jensen.codereview.skill.base.BaseAISkill;
import com.jensen.codereview.skill.base.SkillContext;
import com.jensen.codereview.skill.base.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName ComplexitySkill
 * @Description AI 复杂度分析
 * @Author Jensen
 * @Date 2026/4/14 10:54
 */
@Slf4j
@Component
public class ComplexitySkill extends BaseAISkill {

    /**
     * 构造函数
     *
     * @param chatClient 聊天客户端
     */
    public ComplexitySkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     *
     * @return 技能名称
     */
    @Override
    public String getName() {
        return "AI 复杂度分析";
    }

    /**
     * 技能描述
     *
     * @return 技能描述
     */
    @Override
    public String getDescription() {
        return "使用 AI 深度分析圈复杂度、嵌套深度、方法长度等复杂度指标";
    }

    /**
     * 执行技能
     *
     * @param context 技能上下文
     * @return 技能结果
     */
    @Override
    public CompletableFuture<SkillResult> execute(SkillContext context) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            String code = context.getCodeContent();
            
            List<SkillResult.Issue> issues = analyzeWithAI(code);
            return buildResult(issues, startTime);
        });
    }

    /**
     * 获取 AI 提示
     * @param code 代码内容
     * @return 提示词
     */
    @Override
    protected String getAIPrompt(String code) {
        String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
        
        return String.format("""
                你是一位资深的代码复杂度分析专家。请分析以下代码的复杂度问题：
                
                ```java
                %s
                ```
                
                请从以下维度检查复杂度问题：
                1. 圈复杂度：方法中分支语句（if/else/while/for/case/catch）是否过多，建议不超过10
                2. 嵌套深度：if/for/while 嵌套是否过深，建议不超过3层
                3. 方法长度：单个方法行数是否过多，建议不超过50行
                4. 参数数量：方法参数是否过多，建议不超过5个
                5. 类职责：类是否承担过多职责，是否有多个关注点
                6. 依赖关系：类之间的耦合度是否过高
                7. 重复代码：是否有相似的代码块可以提取
                8. 魔法数字：是否有硬编码的数字应该定义为常量
                9. 条件复杂性：是否有过于复杂的布尔表达式
                10. 控制流：是否有过多的 return/break/continue 语句
                11. 数据复杂度：是否有过多的实例变量或类变量
                12. 继承层次：继承层级是否过深
                
                对于每个问题，返回 JSON 格式：
                [
                  {
                    "severity": "MEDIUM",
                    "lineNumber": 10,
                    "description": "问题描述",
                    "fixSuggestion": "修复建议"
                  }
                ]
                
                severity 可选值：CRITICAL/HIGH/MEDIUM/LOW
                如果没有问题，返回空数组 []。
                只返回 JSON 数组，不要其他内容。
                """, codeSnippet);
    }

    /**
     * 技能类别
     * @return 技能类别
     */
    @Override
    protected String getCategory() {
        return "COMPLEXITY";
    }

    /**
     * 技能优先级
     * @return 技能优先级
     */
    @Override
    public int getPriority() {
        return 12;
    }
}
