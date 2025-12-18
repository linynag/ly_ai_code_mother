package com.ly.lyaicodemother.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProjectDownloadService {
    /**
     * @author:LY  @date: 2025/12/18
     * @param projectPath
     * @param downloadFileName
     * @param response
     * @Return: void
     **/
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
