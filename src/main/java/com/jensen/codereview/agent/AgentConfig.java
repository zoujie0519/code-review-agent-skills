/*
 * All rights Reserved, Designed By Jensen
 * @Title:  AgentConfig.java
 * @Package com.jensen.codereview.agent
 * @author: Jensen
 * @date:   2026/4/14 10:34
 * @version V1.0
 */
package com.jensen.codereview.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @ClassName AgentConfig
 * @Description 配置
 * @Author Jensen
 * @Date 2026/4/14 10:34
 */
@Slf4j
@Configuration
public class AgentConfig {

    @Value("${spring.ai.openai.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model:ark-code-latest}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature:0.3}")
    private Double temperature;

    @Value("${spring.ai.openai.chat.completions-path:/openai-completions}")
    private String completionsPath;

    /**
     * 创建OpenAiApi
     * @return OpenAiApi
     */
    @Bean
    public OpenAiApi openAiApi() {
        log.info("=== Creating OpenAiApi ===");
        log.info("Base URL: {}", baseUrl);
        log.info("Completions Path: {}", completionsPath);
        
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .completionsPath(completionsPath)
                .build();
    }

    /**
     * 创建OpenAiChatModel
     * @param openAiApi OpenAiApi
     * @return OpenAiChatModel
     */
    @Bean
    @Primary
    public OpenAiChatModel openAiChatModel(OpenAiApi openAiApi) {
        log.info("=== Creating OpenAiChatModel ===");
        log.info("Model: {}", model);
        log.info("Base URL: {}", baseUrl);
        log.info("API Key: {}", apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "null");

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();

        log.info("Options: {}", options);
        
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    /**
     * 创建ChatClient
     * @param chatModel chatModel
     * @return ChatClient
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    你是一个资深的Java架构师和代码审查专家。
                    你的职责是提供专业、准确、建设性的代码审查意见。
                    回答要简洁明了，重点突出。
                    """)
                .build();
    }
}
