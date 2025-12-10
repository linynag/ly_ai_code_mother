package com.ly.lyaicodemother.core;


import com.ly.lyaicodemother.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Autowired
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode() {
        String userMessage = "请生成一个简单的多文件项目，包含 HTML、CSS 和 JS 文件，不超过 100 行代码";
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.MULTI_FILE;
        Long appId = 1L;
        File savedDir = aiCodeGeneratorFacade.generateAndSaveCode(userMessage, codeGenTypeEnum, appId);
        assertTrue(savedDir.exists());
    }

    @Test
    void generateAndSaveCodeStream() {
        Long appId = 1L;
        Flux<String> codeStream =
                aiCodeGeneratorFacade.generateAndSaveCodeStream("任务记录网站，不超过20行", CodeGenTypeEnum.MULTI_FILE, appId);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(
                "简单的任务记录网站，总代码量不超过 100 行",
                CodeGenTypeEnum.VUE_PROJECT, 2L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

}