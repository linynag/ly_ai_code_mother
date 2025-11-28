package com.ly.lyaicodemother.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ly.lyaicodemother.constant.AppConstant;
import com.ly.lyaicodemother.exception.BusinessException;
import com.ly.lyaicodemother.exception.ErrorCode;
import com.ly.lyaicodemother.exception.ThrowUtils;
import com.ly.lyaicodemother.mapper.AppMapper;
import com.ly.lyaicodemother.model.dto.app.AppQueryRequest;
import com.ly.lyaicodemother.model.entity.App;
import com.ly.lyaicodemother.model.entity.User;
import com.ly.lyaicodemother.model.vo.AppVO;
import com.ly.lyaicodemother.service.AppService;
import com.ly.lyaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author linyang
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Override
    public Long createApp(App app, HttpServletRequest request) {
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 设置创建用户
        app.setUserId(loginUser.getId());
        // 设置默认值
        app.setPriority(0);
        app.setIsDelete(0);
        // 保存应用
        boolean result = this.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return app.getId();
    }

    @Override
    public boolean updateUserApp(App app, HttpServletRequest request) {
        ThrowUtils.throwIf(app == null || app.getId() == null, ErrorCode.PARAMS_ERROR);
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 检查应用是否存在
        App oldApp = this.getById(app.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 检查是否是应用的创建者
        ThrowUtils.throwIf(!oldApp.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // 只允许修改应用名称
        App updateApp = new App();
        updateApp.setId(app.getId());
        updateApp.setAppName(app.getAppName());
        updateApp.setEditTime(LocalDateTime.now());
        boolean result = this.updateById(updateApp);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean deleteUserApp(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 检查应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 检查是否是应用的创建者
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // 逻辑删除
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public App getAppById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return this.getById(id);
    }

    @Override
    public AppVO getAppVO(Long id) {
        App app = this.getAppById(id);
        return getAppVO(app);
    }

    @Override
    public List<AppVO> listUserApps(AppQueryRequest appQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // 设置查询条件
        appQueryRequest.setUserId(loginUser.getId());
        // 设置分页参数
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = Math.min(appQueryRequest.getPageSize(), 20); // 最多20条
        // 构建查询条件
        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 执行查询
        Page<App> appPage = this.page(Page.of(pageNum, pageSize), queryWrapper);
        return getAppVOList(appPage.getRecords());
    }

    @Override
    public List<AppVO> listFeaturedApps(AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 设置精选应用查询条件
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        // 设置分页参数
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = Math.min(appQueryRequest.getPageSize(), 20); // 最多20条
        // 构建查询条件
        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 按优先级降序排序
        queryWrapper.orderBy("priority", false);
        // 执行查询
        Page<App> appPage = this.page(Page.of(pageNum, pageSize), queryWrapper);
        return getAppVOList(appPage.getRecords());
    }

    @Override
    public boolean deleteAppByAdmin(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 检查应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 逻辑删除
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean updateAppByAdmin(App app) {
        ThrowUtils.throwIf(app == null || app.getId() == null, ErrorCode.PARAMS_ERROR);
        // 检查应用是否存在
        App oldApp = this.getById(app.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新应用
        app.setEditTime(LocalDateTime.now());
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public List<AppVO> listAppsByAdmin(AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 管理员查询不限制分页大小
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        // 构建查询条件
        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 执行查询
        Page<App> appPage = this.page(Page.of(pageNum, pageSize), queryWrapper);
        return getAppVOList(appPage.getRecords());
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return List.of();
        }
        // 获取创建用户id列表
        Set<Long> userIdSet = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        // 批量查询用户信息
        Map<Long, User> userMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        // 转换为视图对象
        return appList.stream().map(app -> {
            AppVO appVO = AppVO.objToVo(app);
            User user = userMap.get(app.getUserId());
            if (user != null) {
                appVO.setUser(userService.getUserVO(user));
            }
            return appVO;
        }).collect(Collectors.toList());
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = AppVO.objToVo(app);
        // 获取创建用户信息
        User user = userService.getById(app.getUserId());
        if (user != null) {
            appVO.setUser(userService.getUserVO(user));
        }
        return appVO;
    }

    @Override
    public void validApp(App app, boolean add) {
        if (app == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String appName = app.getAppName();
        String initPrompt = app.getInitPrompt();
        String codeGenType = app.getCodeGenType();
        String deployKey = app.getDeployKey();

        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isBlank(appName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能为空");
            }
            if (StringUtils.isBlank(initPrompt)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "初始化提示词不能为空");
            }
            if (StringUtils.isBlank(codeGenType)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型不能为空");
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(appName) && appName.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称过长");
        }
        if (StringUtils.isNotBlank(initPrompt) && initPrompt.length() > 2000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "初始化提示词过长");
        }
        if (StringUtils.isNotBlank(deployKey) && deployKey.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "部署密钥过长");
        }
    }

}
