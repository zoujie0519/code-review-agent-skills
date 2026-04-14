/*
 * All rights Reserved, Designed By Jensen
 * @Title:  CodeParser.java
 * @Package com.jensen.codereview.utils
 * @author: Jensen
 * @date:   2026/4/14 11:05
 * @version V1.0
 */
package com.jensen.codereview.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName CodeParser
 * @Description 代码解析器
 * @Author Jensen
 * @Date 2026/4/14 11:05
 */
@Slf4j
public class CodeParser {

    /**
     * 检测编程语言
     * @param code 代码内容
     * @return 语言类型
     */
    public static String detectLanguage(String code) {
        if (code.contains("package ") && code.contains("class ")) {
            return "java";
        } else if (code.contains("import ") && (code.contains("def ") || code.contains("class "))) {
            return "python";
        } else if (code.contains("function ") || code.contains("const ") || code.contains("let ")) {
            return "javascript";
        }
        return "unknown";
    }

    /**
     * 提取类名
     * @param code 代码内容
     * @return 类名
     */
    public static String extractClassName(String code) {
        Pattern pattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 提取方法名列表
     * @param code 代码内容
     * @return 方法名列表
     */
    public static java.util.List<String> extractMethodNames(String code) {
        java.util.List<String> methods = new java.util.ArrayList<>();
        Pattern pattern = Pattern.compile("(?:public|private|protected)\\s+\\w+\\s+(\\w+)\\s*\\(");
        Matcher matcher = pattern.matcher(code);
        while (matcher.find()) {
            methods.add(matcher.group(1));
        }
        return methods;
    }

    /**
     * 计算代码行数
     * @param code 代码内容
     * @return 行数
     */
    public static int countLines(String code) {
        if (code == null || code.isEmpty()) {
            return 0;
        }
        return code.split("\n").length;
    }

    /**
     * 计算注释行数
     * @param code 代码内容
     * @return 注释行数
     */
    public static int countCommentLines(String code) {
        int count = 0;
        String[] lines = code.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("//") || trimmed.startsWith("*") || 
                trimmed.startsWith("/*") || trimmed.startsWith("*/")) {
                count++;
            }
        }
        return count;
    }
}
