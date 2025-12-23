package com.ly.lyaicodemother.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ly.lyaicodemother.ai.tools.ToolManager;
import com.ly.lyaicodemother.exception.BusinessException;
import com.ly.lyaicodemother.exception.ErrorCode;
import com.ly.lyaicodemother.model.enums.CodeGenTypeEnum;
import com.ly.lyaicodemother.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
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


    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
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
    private StreamingChatModel openAiStreamingChatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private StreamingChatModel reasoningStreamingChatModel;
    @Resource
    private ToolManager toolManager;


    /**
     * 创建默认的 AiCodeGeneratorService 实例
     * 这个方法是为了兼容旧版本的代码，旧版本的代码中没有 codeGenTypeEnum 参数
     * @return AiCodeGeneratorService 实例
     **/
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return createAiCodeGeneratorService(1L, CodeGenTypeEnum.HTML);
    }


    /**
     * 根据应用ID获取 AiCodeGeneratorService 实例
     * @param appId 应用ID
     * @return AiCodeGeneratorService 实例
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    private String buildCacheKey(Long appId, CodeGenTypeEnum codeGenTypeEnum) {
        return appId + "_" + codeGenTypeEnum.getValue();
    }

    /**
     * 根据appId创建不同的 AiCodeGeneratorService 实例
     * @param appId 应用ID
     * @return AiCodeGeneratorService 实例
     */
    public AiCodeGeneratorService createAiCodeGeneratorService(Long appId) {
        // 根据appId创建不同的聊天内存
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(50)
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
        // 加载应用的对话历史到内存中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);

        // 根据appId创建不同的AiCodeGeneratorService实例

        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }

    public AiCodeGeneratorService createAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenTypeEnum) {
        // 根据appId创建不同的聊天内存
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(50)
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
        // 加载应用的对话历史到内存中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);

        // 根据appId创建不同的AiCodeGeneratorService实例

        return switch (codeGenTypeEnum) {
            // VUE使用的推理模型
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(toolManager.getAllTools())
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()
                    ))
                    .build();

            case HTML, MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型：" + codeGenTypeEnum);

        };
    }


}
