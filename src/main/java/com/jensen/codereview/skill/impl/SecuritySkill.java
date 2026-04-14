/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SecuritySkill.java
 * @Package com.jensen.codereview.skill.impl
 * @author: Jensen
 * @date:   2026/4/14 10:27
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
 * @ClassName SecuritySkill
 * @Description AI 安全漏洞扫描
 * @Author Jensen
 * @Date 2026/4/14 10:27
 */
@Slf4j
@Component
public class SecuritySkill extends BaseAISkill {

    /**
     * 构造函数
     * @param chatClient 聊天客户端
     */
    public SecuritySkill(ChatClient chatClient) {
        super(chatClient);
    }

    /**
     * 技能名称
     * @return 技能名称
     */
    @Override
    public String getName() { 
        return "AI 安全漏洞扫描"; 
    }

    /**
     * 技能描述
     * @return 技能描述
     */
    @Override
    public String getDescription() { 
        return "使用 AI 深度检测SQL注入、XSS、硬编码密码等安全漏洞"; 
    }

    /**
     * 技能执行
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
     * 获取AI提示
     * @param code 代码
     * @return AI提示
     */
    @Override
    protected String getAIPrompt(String code) {
        String codeSnippet = code.length() > 3000 ? code.substring(0, 3000) + "\n...[代码过长，已截断]" : code;
        
        return String.format("""
                你是一位资深的安全专家。请分析以下代码的安全漏洞：
                
                ```java
                %s
                ```
                
                请从以下维度检查安全问题：
                1. SQL注入：是否使用字符串拼接构建SQL语句
                2. XSS攻击：是否有未转义的用户输入输出
                3. 硬编码凭证：密码、密钥、Token是否硬编码在代码中
                4. 路径遍历：文件操作是否有路径验证
                5. 敏感信息泄露：日志中是否打印敏感信息
                6. 不安全的随机数：是否使用SecureRandom生成安全相关的随机数
                7. 反序列化漏洞：是否有不安全的反序列化操作
                8. 权限控制：是否有缺失的权限校验
                9. 加密算法：是否使用了不安全的加密算法（如MD5、SHA1）
                10. 资源泄漏：数据库连接、IO流是否正确关闭
                
                对于每个问题，返回 JSON 格式：
                [
                  {
                    "severity": "CRITICAL",
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
     * @return 技能类别
     */
    @Override
    protected String getCategory() {
        return "SECURITY";
    }

    /**
     * 获取技能优先级
     * @return 技能优先级
     */
    @Override
    public int getPriority() { 
        return 5; 
    }
}
