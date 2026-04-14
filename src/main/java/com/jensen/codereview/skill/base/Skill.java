/*
 * All rights Reserved, Designed By Jensen
 * @Title:  Skill.java
 * @Package com.jensen.codereview.skill.base
 * @author: Jensen
 * @date:   2026/4/14 10:14
 * @version V1.0
 */
package com.jensen.codereview.skill.base;

import java.util.concurrent.CompletableFuture;

/**
 * @ClassName Skill
 * @Description 技能接口
 * @Author Jensen
 * @Date 2026/4/14 10:14
 */
public interface Skill {

    /**
     * 技能名称
     * @return 技能名称
     */
    String getName();

    /**
     * 技能描述
     * @return 技能描述
     */
    String getDescription();

    /**
     * 技能执行
     * @param context 上下文
     * @return 执行结果
     */
    CompletableFuture<SkillResult> execute(SkillContext context);

    /**
     * 技能优先级
     * @return 优先级
     */
    default int getPriority() { return 100; }

    /**
     * 技能是否启用
     * @return  true:启用
     */
    default boolean isEnabled() { return true; }
}
