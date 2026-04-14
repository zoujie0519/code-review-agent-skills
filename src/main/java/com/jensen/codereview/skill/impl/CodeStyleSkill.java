/*
 * All rights Reserved, Designed By Jensen
 * @Title:  CodeStyleSkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:25
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
 * @ClassName CodeStyleSkill
 * @Description AI 代码风格检查
 * @Author Jensen
 * @Date 2026/4/14 10:26
 */
@Slf4j
@Component
public class CodeStyleSkill extends BaseAISkill {

    /**
     * 构造函数
     *
     * @param chatClient 聊天客户端
     */
    public CodeStyleSkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     *
     * @return 技能名称
     */
    @Override
    public String getName() { 
        return "AI 代码风格检查"; 
    }

    /**
     * 技能描述
     *
     * @return 技能描述
     */
    @Override
    public String getDescription() { 
        return "使用 AI 深度检查命名规范、代码结构、格式化等风格问题"; 
    }

    /**
     * 执行技能
     *
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
     * 获取 AI 提示词
     *
     * @param code 待检查的代码
     * @return 提示词
     */
    @Override
    protected String getAIPrompt(String code) {
        String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
        
        return String.format("""
                你是一位资深的 Java 代码风格专家。请分析以下代码的风格问题：
                
                ```java
                %s
                ```
                
                请从以下维度检查代码风格：
                1. 命名规范：类名、方法名、变量名是否符合 Java 命名约定
                2. 代码格式：缩进、空格、换行是否合理
                3. 代码长度：单行代码是否过长（建议不超过120字符）
                4. 注释风格：注释是否清晰、位置是否合适
                5. 代码组织：import 顺序、类结构是否合理
                6. 常量定义：是否正确使用 final 和 static
                7. 可见性修饰符：是否合理使用了 public/private/protected
                
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
     * 获取问题类别
     *
     * @return 类别
     */
    @Override
    protected String getCategory() {
        return "STYLE";
    }

    /**
     * 获取技能优先级
     *
     * @return 技能优先级
     */
    @Override
    public int getPriority() { 
        return 10; 
    }
}
