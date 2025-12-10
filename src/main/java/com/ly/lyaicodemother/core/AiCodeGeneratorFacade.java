package com.ly.lyaicodemother.core;

import com.ly.lyaicodemother.ai.AiCodeGeneratorService;
import com.ly.lyaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.ly.lyaicodemother.ai.model.HtmlCodeResult;
import com.ly.lyaicodemother.ai.model.MultiFileCodeResult;
import com.ly.lyaicodemother.core.parse.CodeParserExecutor;
import com.ly.lyaicodemother.core.saver.CodeFileSaverExecutor;
import com.ly.lyaicodemother.exception.BusinessException;
import com.ly.lyaicodemother.exception.ErrorCode;
import com.ly.lyaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 id
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        // 根据应用 id 获取 AI 代码生成服务实例
        // AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        switch (codeGenTypeEnum) {
            case HTML:
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                return CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
            case MULTI_FILE:
                MultiFileCodeResult multiFileResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                return CodeFileSaverExecutor.executeSaver(multiFileResult, CodeGenTypeEnum.MULTI_FILE, appId);
            case VUE_PROJECT:
                Flux<String> vueProjectCodeStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                return CodeFileSaverExecutor.executeSaver(vueProjectCodeStream, CodeGenTypeEnum.VUE_PROJECT, appId);
            default:
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
        }
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId           应用 id
     * @return 流式响应
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        // 根据应用 id 获取 AI 代码生成服务实例
        // AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        // AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        switch (codeGenTypeEnum) {
            case HTML:
                Flux<String> htmlCodeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                return processCodeStream(htmlCodeStream, CodeGenTypeEnum.HTML, appId);
            case MULTI_FILE:
                Flux<String> multiFileCodeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                return processCodeStream(multiFileCodeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            case VUE_PROJECT:
                Flux<String> vueProjectCodeStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                return processCodeStream(vueProjectCodeStream, CodeGenTypeEnum.VUE_PROJECT, appId);
            default:
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
        }
    }


    /**
     * 通用流式代码处理方法
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @param appId       应用 id
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            // 流式返回完成后保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                // 使用执行器保存代码
                File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType, appId);
                log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }


    // /**
    //  * 生成 HTML 模式的代码并保存（流式）
    //  *
    //  * @param userMessage 用户提示词
    //  * @return 保存的目录
    //  */
    // @Deprecated
    // private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
    //     // 调用 AI 服务生成 HTML 代码流
    //     Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
    //     // 当流式返回生成代码完成后，再保存代码
    //     StringBuilder codeBuilder = new StringBuilder();
    //     return result
    //             .doOnNext(chunk -> {
    //                 // 实时收集代码片段
    //                 codeBuilder.append(chunk);
    //             })
    //             .doOnComplete(() -> {
    //                 // 流式返回完成后保存代码
    //                 try {
    //                     String completeHtmlCode = codeBuilder.toString();
    //                     HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
    //                     // 保存代码到文件
    //                     File savedDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    //                     log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
    //                 } catch (Exception e) {
    //                     log.error("保存失败: {}", e.getMessage());
    //                 }
    //             });
    // }
    //
    // /**
    //  * 生成多文件模式的代码并保存（流式）
    //  *
    //  * @param userMessage 用户提示词
    //  * @return 保存的目录
    //  */
    // @Deprecated
    // private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
    //     AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.aiCodeGeneratorService(appId);
    //     Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
    //     // 当流式返回生成代码完成后，再保存代码
    //     StringBuilder codeBuilder = new StringBuilder();
    //     return result
    //             .doOnNext(chunk -> {
    //                 // 实时收集代码片段
    //                 codeBuilder.append(chunk);
    //             })
    //             .doOnComplete(() -> {
    //                 // 流式返回完成后保存代码
    //                 try {
    //                     String completeMultiFileCode = codeBuilder.toString();
    //                     MultiFileCodeResult multiFileResult = CodeParser.parseMultiFileCode(completeMultiFileCode);
    //                     // 保存代码到文件
    //                     File savedDir = CodeFileSaver.saveMultiFileCodeResult(multiFileResult);
    //                     log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
    //                 } catch (Exception e) {
    //                     log.error("保存失败: {}", e.getMessage());
    //                 }
    //             });
    // }


}
