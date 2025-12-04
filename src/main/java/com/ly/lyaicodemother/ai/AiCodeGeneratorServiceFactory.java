package com.ly.lyaicodemother.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ly.lyaicodemother.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {


    private final Cache<Long, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，key: {}, value: {}, cause: {}", key, value, cause);
            })
            .build();


    @Resource
    private ChatModel chatModel;
    @Resource
    private StreamingChatModel streamingChatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;


    /**
     * 创建默认的 AiCodeGeneratorService 实例
     * @return AiCodeGeneratorService 实例
     **/
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }


    /**
     * 根据应用ID获取 AiCodeGeneratorService 实例
     * @param appId 应用ID
     * @return AiCodeGeneratorService 实例
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return serviceCache.get(appId, this::createAiCodeGeneratorService);
    }

    /**
     * 根据appId创建不同的 AiCodeGeneratorService 实例
     * @param appId 应用ID
     * @return AiCodeGeneratorService 实例
     */
    public AiCodeGeneratorService createAiCodeGeneratorService(Long appId) {
        // 根据appId创建不同的聊天内存
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
        // 加载应用的对话历史到内存中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);

        // 根据appId创建不同的AiCodeGeneratorService实例
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
}
