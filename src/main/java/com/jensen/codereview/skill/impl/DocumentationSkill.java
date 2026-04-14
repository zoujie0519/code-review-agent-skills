/*
 * All rights Reserved, Designed By Jensen
 * @Title:  DocumentationSkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:56
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
 * @ClassName DocumentationSkill
 * @Description AI 文档检查
 * @Author Jensen
 * @Date 2026/4/14 10:56
 */
@Slf4j
@Component
public class DocumentationSkill extends BaseAISkill {

    /**
     * 构造函数
     *
     * @param chatClient 聊天客户端
     */
    public DocumentationSkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     *
     * @return 技能名称
     */
    @Override
    public String getName() {
        return "AI 文档检查";
    }

    /**
     * 技能描述
     *
     * @return 技能描述
     */
    @Override
    public String getDescription() {
        return "使用 AI 深度检查 JavaDoc、注释质量、TODO标记等";
    }

    /**
     * 技能执行
     *
     * @param context 上下文
     * @return 执行结果
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
     *
     * @param code 代码
     * @return AI 提示
     */
    @Override
    protected String getAIPrompt(String code) {
        String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
        
        return String.format("""
                你是一位资深的 Java 文档规范专家。请分析以下代码的文档问题：
                
                ```java
                %s
                ```
                
                请从以下维度检查文档问题：
                1. 类注释：公共类是否有完整的类级别 Javadoc，说明类的用途和职责
                2. 方法注释：公共方法是否有 Javadoc，包含 @param、@return、@throws
                3. 参数说明：方法参数是否有清晰的 @param 说明
                4. 返回值说明：是否有 @return 说明返回值的含义
                5. 异常说明：抛出的异常是否有 @throws 说明
                6. 注释质量：注释是否清晰、准确、有意义，避免废话注释
                7. 过时注释：注释是否与代码不一致，是否存在误导性注释
                8. TODO标记：是否有未处理的 TODO/FIXME/HACK 标记
                9. 魔法数字：硬编码的数字是否有注释说明含义
                10. 复杂逻辑：复杂的业务逻辑是否有行内注释说明
                11. 接口文档：接口方法是否有完整的使用说明和示例
                12. 版本信息：重要类是否有 @since、@author、@version 标记
                
                对于每个问题，返回 JSON 格式：
                [
                  {
                    "severity": "LOW",
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
     * 获取技能类别
     *
     * @return 技能类别
     */
    @Override
    protected String getCategory() {
        return "DOCUMENTATION";
    }

    /**
     * 获取技能优先级
     *
     * @return 技能优先级
     */
    @Override
    public int getPriority() {
        return 25;
    }
}
