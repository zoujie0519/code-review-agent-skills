/*
 * All rights Reserved, Designed By Jensen
 * @Title:  SkillExecutionException.java
 * @Package com.jensen.codereview.exception
 * @author: Jensen
 * @date:   2026/4/14 11:01
 * @version V1.0
 */
package com.jensen.codereview.exception;

/**
 * @ClassName SkillExecutionException
 * @Description 技能执行异常
 * @Author Jensen
 * @Date 2026/4/14 11:01
 */
public class SkillExecutionException extends RuntimeException {

    public SkillExecutionException(String skillName, String message) {
        super(String.format("技能[%s]执行失败: %s", skillName, message));
    }

    public SkillExecutionException(String skillName, Throwable cause) {
        super(String.format("技能[%s]执行失败", skillName), cause);
    }
}
