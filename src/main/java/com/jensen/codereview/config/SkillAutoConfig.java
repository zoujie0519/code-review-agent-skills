/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SkillAutoConfig.java
 * @Package com.jensen.codereview.config
 * @author: Jensen
 * @date:   2026/4/14 15:18
 * @version V1.0
 */
package com.jensen.codereview.config;

import com.jensen.codereview.skill.base.Skill;
import com.jensen.codereview.skill.registry.SkillRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import java.util.List;

/**
 * @ClassName SkillAutoConfig
 * @Description 技能自动配置
 * @Author Jensen
 * @Date 2026/4/14 10:41
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@DependsOn("skillRegistry")
public class SkillAutoConfig {

    /**
     * 技能注册中心
     */
    private final SkillRegistry skillRegistry;

    /**
     * 技能列表
     */
    private final List<Skill> skills;

    /**
     * 注册技能
     */
    @PostConstruct
    public void registerSkills() {
        skillRegistry.registerAll(skills);
        log.info("✅ 已自动注册 {} 个技能", skills.size());
    }
}
