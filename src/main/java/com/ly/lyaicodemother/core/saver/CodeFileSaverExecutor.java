package com.ly.lyaicodemother.core.saver;

import com.ly.lyaicodemother.ai.model.HtmlCodeResult;
import com.ly.lyaicodemother.ai.model.MultiFileCodeResult;
import com.ly.lyaicodemother.exception.BusinessException;
import com.ly.lyaicodemother.exception.ErrorCode;
import com.ly.lyaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 *
 * @author yupi
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();

    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaver = new MultiFileCodeFileSaverTemplate();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @param appId       应用 id
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        switch (codeGenType) {
            case HTML:
                return htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE:
                return multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        }
    }
}
