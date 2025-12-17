package com.ly.lyaicodemother.service;

import org.springframework.stereotype.Service;

@Service
public interface ScreenshotService {
    /**
     * 生成并且上传截图
     * @param webUrl
     * @return
     */
    String generateAndUploadScreenshot(String webUrl);
}
