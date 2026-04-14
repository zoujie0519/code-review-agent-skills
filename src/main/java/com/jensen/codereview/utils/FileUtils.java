/*
 * All rights Reserved, Designed By Jensen
 * @Title:  FileUtils.java
 * @Package com.jensen.codereview.utils
 * @author: Jensen
 * @date:   2026/4/14 11:06
 * @version V1.0
 */
package com.jensen.codereview.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName FileUtils
 * @Description 文件工具类
 * @Author Jensen
 * @Date 2026/4/14 11:06
 */
@Slf4j
public class FileUtils {

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readString(path);
        } catch (IOException e) {
            log.error("读取文件失败: {}", filePath, e);
            throw new RuntimeException("读取文件失败: " + filePath, e);
        }
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * 验证文件大小
     * @param content 文件内容
     * @param maxSizeInMB 最大大小(MB)
     * @return 是否有效
     */
    public static boolean validateFileSize(String content, int maxSizeInMB) {
        long sizeInBytes = content.getBytes().length;
        long maxSizeInBytes = (long) maxSizeInMB * 1024 * 1024;
        return sizeInBytes <= maxSizeInBytes;
    }
}
