/*
 * All rights Reserved, Designed By Jensen
 * @Title:  WebConfig.java
 * @Package com.jensen.codereview.config
 * @author: Jensen
 * @date:   2026/4/14 10:46
 * @version V1.0
 */
package com.jensen.codereview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName WebConfig
 * @Description Web配置
 * @Author Jensen
 * @Date 2026/4/14 10:46
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 添加资源处理器，忽略 favicon.ico 请求
     */
    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
    }
}
