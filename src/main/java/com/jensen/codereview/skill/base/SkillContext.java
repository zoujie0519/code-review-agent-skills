/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SkillContext.java
 * @Package com.jensen.codereview.skill.base
 * @author: Jensen
 * @date:   2026/4/14 10:17
 * @version V1.0
 */
package com.jensen.codereview.skill.base;

import lombok.Builder;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SkillContext
 * @Description 技能上下文
 * @Author Jensen
 * @Date 2026/4/14 10:17
 */
@Data
@Builder
public class SkillContext {

    /**
     * 代码内容
     */
    private String codeContent;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 语言
     */
    private String language;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 共享数据
     */
    private Map<String, Object> sharedData;

    /**
     * 创建技能上下文
     *
     * @param codeContent 代码内容
     * @return SkillContext 技能上下文
     */
    public static SkillContext create(String codeContent) {
        return SkillContext.builder()
                .codeContent(codeContent)
                .language("java")
                .metadata(new HashMap<>())
                .sharedData(new HashMap<>())
                .build();
    }

    /**
     * 添加共享数据
     *
     * @param key   键
     * @param value 值
     */
    public void putShared(String key, Object value) {
        if (sharedData == null) {
            sharedData = new HashMap<>();
        }
        sharedData.put(key, value);
    }

    /**
     * 获取共享数据
     *
     * @param key 键
     * @return T 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getShared(String key) {
        return sharedData == null ? null : (T) sharedData.get(key);
    }
}
