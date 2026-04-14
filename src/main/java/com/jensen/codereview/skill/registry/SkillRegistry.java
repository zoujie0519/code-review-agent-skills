/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SkillRegistry.java
 * @Package com.jensen.codereview.skill.registry
 * @author: Jensen
 * @date:   2026/4/14 10:21
 * @version V1.0
 */
package com.jensen.codereview.skill.registry;

import com.jensen.codereview.skill.base.Skill;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SkillRegistry
 * @Description 技能注册中心
 * @Author Jensen
 * @Date 2026/4/14 10:22
 */
@Slf4j
@Component
public class SkillRegistry {

    /**
     * 技能列表
     */
    private final Map<String, Skill> skills = new ConcurrentHashMap<>();

    /**
     * 注册技能
     * @param skill 技能
     */
    public void register(Skill skill) {
        skills.put(skill.getName(), skill);
        log.info("注册技能: {} - {}", skill.getName(), skill.getDescription());
    }

    /**
     * 注册技能列表
     * @param skillList 技能列表
     */
    public void registerAll(List<Skill> skillList) {
        skillList.forEach(this::register);
    }

    /**
     * 获取技能
     * @param name 技能名称
     * @return 技能
     */
    public Optional<Skill> getSkill(String name) {
        return Optional.ofNullable(skills.get(name));
    }

    /**
     * 获取所有技能
     * @return 技能列表
     */
    public List<Skill> getEnabledSkills() {
        return skills.values().stream()
                .filter(Skill::isEnabled)
                .sorted(Comparator.comparingInt(Skill::getPriority))
                .toList();
    }

    /**
     * 技能列表
     */
    @PostConstruct
    public void listSkills() {
        log.info("=== 已注册技能列表 ===");
        skills.values().forEach(skill ->
                log.info("- {}: {}", skill.getName(), skill.getDescription())
        );
    }
}
