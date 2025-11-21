package com.ly.lyaicodemother.ai;

import com.ly.lyaicodemother.ai.model.HtmlCodeResult;
import com.ly.lyaicodemother.ai.model.MultiFileCodeResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Autowired
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult htmlCode = aiCodeGeneratorService.generateHtmlCode("请生成一个简单的 HTML 页面，不超过20行");
        assertNotNull(htmlCode);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult multiFileCode = aiCodeGeneratorService.generateMultiFileCode("请生成一个简单的多文件项目，不超过20行");
        assertNotNull(multiFileCode);
    }
}