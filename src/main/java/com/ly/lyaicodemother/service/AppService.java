package com.ly.lyaicodemother.service;

import com.ly.lyaicodemother.model.dto.app.AppAddRequest;
import com.ly.lyaicodemother.model.dto.app.AppQueryRequest;
import com.ly.lyaicodemother.model.entity.App;
import com.ly.lyaicodemother.model.entity.User;
import com.ly.lyaicodemother.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author linyang
 */
public interface AppService extends IService<App> {
    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    void generateAppScreenshotAsync(Long appId, String appUrl);

    /**
     * 创建应用
     *
     * @param app 应用信息
     * @param request 请求
     * @return 应用id
     */
    Long createApp(App app, HttpServletRequest request);

    /**
     * 根据id修改应用（用户）
     *
     * @param app 应用信息
     * @param request 请求
     * @return 是否成功
     */
    boolean updateUserApp(App app, HttpServletRequest request);

    /**
     * 根据id删除应用（用户）
     *
     * @param id 应用id
     * @param request 请求
     * @return 是否成功
     */
    boolean deleteUserApp(Long id, HttpServletRequest request);

    /**
     * 根据id获取应用详情
     *
     * @param id 应用id
     * @return 应用信息
     */
    App getAppById(Long id);

    /**
     * 根据id获取应用视图
     *
     * @param id 应用id
     * @return 应用视图
     */
    AppVO getAppVO(Long id);

    /**
     * 分页查询用户的应用列表
     *
     * @param appQueryRequest 查询请求
     * @param request 请求
     * @return 应用分页列表
     */
    List<AppVO> listUserApps(AppQueryRequest appQueryRequest, HttpServletRequest request);

    /**
     * 分页查询精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 精选应用分页列表
     */
    List<AppVO> listFeaturedApps(AppQueryRequest appQueryRequest);

    /**
     * 管理员删除任意应用
     *
     * @param id 应用id
     * @return 是否成功
     */
    boolean deleteAppByAdmin(Long id);

    /**
     * 管理员更新任意应用
     *
     * @param app 应用信息
     * @return 是否成功
     */
    boolean updateAppByAdmin(App app);

    /**
     * 管理员分页查询应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 应用分页列表
     */
    List<AppVO> listAppsByAdmin(AppQueryRequest appQueryRequest);

    /**
     * 获取查询包装器
     *
     * @param appQueryRequest 查询请求
     * @return 查询包装器
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取应用视图列表
     *
     * @param appList 应用列表
     * @return 应用视图列表
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 获取应用视图
     *
     * @param app 应用
     * @return 应用视图
     */
    AppVO getAppVO(App app);

    /**
     * 应用数据校验
     *
     * @param app 应用
     * @param add 是否为新增
     */
    void validApp(App app, boolean add);

    /**
     * 应用对话生成代码
     *
     * @param appId 应用id
     * @param userMessage 用户消息
     * @param LoginUser 登录用户
     * @return 代码流
     */
    Flux<String> chatToGenCode(Long appId, String userMessage, User LoginUser);

    /**
     * 应用部署
     * @param appId 应用id
     * @param loginUser 登录用户
     * @return 部署 URL
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 添加应用
     * @param appAddRequest
     * @param loginUser 登录用户
     * @return 应用id
     */
    Long addApp(AppAddRequest appAddRequest, User loginUser);
}
