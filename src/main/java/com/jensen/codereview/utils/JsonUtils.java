/*
 * All rights Reserved, Designed By Jensen
 * @Title:  JsonUtils.java
 * @Package com.jensen.codereview.utils
 * @author: Jensen
 * @date:   2026/4/14 11:07
 * @version V1.0
 */
package com.jensen.codereview.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName JsonUtils
 * @Description JSON工具类
 * @Author Jensen
 * @Date 2026/4/14 11:07
 */
@Slf4j
public class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * 对象转JSON字符串
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            log.error("对象转JSON失败", e);
            return "{}";
        }
    }

    /**
     * JSON字符串转对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            log.error("JSON转对象失败", e);
            return null;
        }
    }
}
