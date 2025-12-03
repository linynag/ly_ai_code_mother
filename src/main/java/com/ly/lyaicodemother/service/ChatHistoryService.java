package com.ly.lyaicodemother.service;

import com.ly.lyaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ly.lyaicodemother.model.entity.ChatHistory;
import com.ly.lyaicodemother.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author linyang
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     * @param appId  应用id
     * @param message  消息
     * @param messageType  消息类型
     * @param userId  用户id
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 删除应用的对话历史
     * @param appId  应用id
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 获取应用的对话历史
     * @param appId  应用id
     * @param pageSize  每页数量
     * @param lastCreateTime  最后创建时间
     * @param loginUser  登录用户
     * @return 应用的对话历史分页列表
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 获取查询包装器
     * @param chatHistoryQueryRequest  对话历史查询请求
     * @return 查询包装器
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);
}
