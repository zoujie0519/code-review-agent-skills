/*
 * All rights Reserved, Designed By Jensen
 * @Title:  BestPracticeSkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:52
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
 * @ClassName BestPracticeSkill
 * @Description AI 最佳实践检查
 * @Author Jensen
 * @Date 2026/4/14 10:52
 */
@Slf4j
@Component
public class BestPracticeSkill extends BaseAISkill {

    /**
     * 创建一个 AI 最佳实践检查技能
     * @param chatClient 聊天客户端
     */
    public BestPracticeSkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     * @return 技能名称
     */
    @Override
    public String getName() {
        return "AI 最佳实践检查";
    }

    /**
     * 技能描述
     * @return 技能描述
     */
    @Override
    public String getDescription() {
        return "使用 AI 深度检查 Java 最佳实践，如设计模式、SOLID原则等";
    }

    /**
     * 执行技能
     * @param context 技能上下文
     * @return 技能执行结果
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
     * @param code 代码
     * @return 提示词
     */
    @Override
    protected String getAIPrompt(String code) {
        String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
        
        return String.format("""
                你是一位资深的 Java 最佳实践专家。请分析以下代码是否符合最佳实践：
                
                ```java
                %s
                ```
                
                请从以下维度检查最佳实践问题：
                1. 设计模式：是否合理使用了设计模式（如工厂、单例、策略等）
                2. SOLID原则：是否符合单一职责、开闭原则、里氏替换等原则
                3. 不可变性：是否合理使用 final 修饰符、不可变对象
                4. 接口编程：是否面向接口编程而非具体实现
                5. 异常处理：是否使用了合适的异常类型、异常处理策略
                6. 日志规范：是否正确使用日志框架、日志级别
                7. 常量定义：是否定义了有意义的常量而非魔法数字/字符串
                8. Lambda使用：是否可以简化为 Lambda 表达式或 Stream API
                9. 泛型使用：是否正确使用了泛型避免类型转换
                10. 资源管理：是否正确使用 try-with-resources 等资源管理
                11. 并发安全：是否正确处理了线程安全问题
                12. 代码复用：是否有重复代码可以提取为公共方法
                
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
        return "BEST_PRACTICE";
    }

    /**
     * 技能优先级
     * @return 技能优先级
     */
    @Override
    public int getPriority() {
        return 15;
    }
}
