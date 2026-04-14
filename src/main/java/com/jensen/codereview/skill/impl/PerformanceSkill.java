/*
 * All rights Reserved, Designed By Jensen
 * @Title:  PerformanceSkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:50
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
 * @ClassName PerformanceSkill
 * @Description AI 性能分析
 * @Author Jensen
 * @Date 2026/4/14 10:50
 */
@Slf4j
@Component
public class PerformanceSkill extends BaseAISkill {

    /**
     * 构造函数
     *
     * @param chatClient 聊天客户端
     */
    public PerformanceSkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     *
     * @return 技能名称
     */
    @Override
    public String getName() {
        return "AI 性能分析";
    }

    /**
     * 技能描述
     *
     * @return 技能描述
     */
    @Override
    public String getDescription() {
        return "使用 AI 深度检测循环优化、集合操作、IO性能等问题";
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
                你是一位资深的 Java 性能优化专家。请分析以下代码的性能问题：
                
                ```java
                %s
                ```
                
                请从以下维度检查性能问题：
                1. 字符串拼接：循环中是否使用 StringBuilder 而非 + 运算符
                2. 集合初始化：是否指定初始容量避免扩容
                3. 对象创建：循环中是否有不必要的对象创建
                4. 数据库查询：是否存在 N+1 查询问题
                5. 缓存使用：重复计算是否可以添加缓存
                6. 算法复杂度：是否有更优的算法（如 O(n²) 改为 O(n log n)）
                7. 同步锁：锁粒度是否过大，是否可以使用并发集合
                8. IO 操作：是否使用了缓冲流，是否正确关闭资源
                9. 日志输出：是否在生产代码中使用 System.out.println
                10. 递归优化：是否有未优化的递归导致栈溢出风险
                11. 流式处理：Stream API 使用是否合理，是否有并行化机会
                12. 内存泄漏：是否有静态集合持续增长、监听器未移除
                
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
        return "PERFORMANCE";
    }

    /**
     * 技能优先级
     * @return 技能优先级
     */
    @Override
    public int getPriority() {
        return 20;
    }
}
