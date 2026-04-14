/*
 * All rights Reserved, Designed By Jensen
 * @Title:  BugDetectionSkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:29
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
 * @ClassName BugDetectionSkill
 * @Description AI Bug 检测
 * @Author Jensen
 * @Date 2026/4/14 10:30
 */
@Slf4j
@Component
public class BugDetectionSkill extends BaseAISkill {

    /**
     * 构造函数
     * @param chatClient 聊天客户端
     */
    public BugDetectionSkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     * @return 技能名称
     */
    @Override
    public String getName() { 
        return "AI Bug 检测"; 
    }

    /**
     * 技能描述
     * @return 技能描述
     */
    @Override
    public String getDescription() { 
        return "使用 AI 深度检测空指针、资源泄漏、异常处理等潜在Bug"; 
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
     * 获取 AI 提示词
     * @param code 代码内容
     * @return 提示词
     */
    @Override
    protected String getAIPrompt(String code) {
        String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
        
        return String.format("""
                你是一位资深的 Java Bug 检测专家。请分析以下代码的潜在 Bug：
                
                ```java
                %s
                ```
                
                请从以下维度检查潜在 Bug：
                1. 空指针风险：是否有未检查 null 的对象访问、Optional 使用不当
                2. 资源泄漏：IO流、数据库连接、HTTP连接是否正确关闭，是否使用 try-with-resources
                3. 并发问题：是否有线程安全问题、竞态条件、死锁风险
                4. 异常处理：是否捕获了不恰当的异常、空 catch 块、异常吞没
                5. 边界条件：数组越界、除零、负数、溢出等
                6. 类型转换：是否有不安全的类型转换、ClassCastException 风险
                7. 集合操作：ConcurrentModificationException、null key/value
                8. 字符串比较：是否错误使用 == 而非 equals()
                9. 死循环：是否有潜在的无限循环、递归栈溢出
                10. 逻辑错误：条件判断错误、运算符优先级问题
                11. 日期时间：时区问题、 SimpleDateFormat 线程不安全
                12. BigDecimal：浮点数运算精度问题
                
                对于每个问题，返回 JSON 格式：
                [
                  {
                    "severity": "HIGH",
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
     * @return 类别
     */
    @Override
    protected String getCategory() {
        return "BUG";
    }

    /**
     * 获取技能优先级
     * @return 技能优先级
     */
    @Override
    public int getPriority() { 
        return 8; 
    }
}
