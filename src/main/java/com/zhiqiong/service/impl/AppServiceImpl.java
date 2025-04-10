package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.enums.ReviewStatusEnum;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.mapper.AppMapper;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.app.AppPageVO;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.app.OperateAppVO;
import com.zhiqiong.model.vo.app.ReviewAppVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.AppService;
import com.zhiqiong.service.QuestionService;
import com.zhiqiong.service.ScoringResultService;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.ThrowExceptionUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, AppEntity> implements AppService {

    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;


    @Override
    public AppVO selectAppInfo(Long id) {
        AppEntity app = this.getById(id);
        ThrowExceptionUtil.throwIf(app == null, ErrorCode.ERROR_PARAM, "应用id错误");
        AppVO appVO = converterVo(app);
        UserVO user = userService.getUserInfo(app.getUserId());
        appVO.setUser(user);
        return appVO;
    }


    @Override
    public List<AppVO> selectAppList(String appName, Integer reviewStatus) {
        LambdaQueryWrapper<AppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(appName), AppEntity::getAppName, appName);
        queryWrapper.eq(reviewStatus != null, AppEntity::getReviewStatus, reviewStatus);
        queryWrapper.orderByDesc(AppEntity::getCreateTime);
        return converterVo(this.list(queryWrapper));
    }

    @Override
    public void addApp(OperateAppVO appVO) {
        checkParams(appVO);
        AppEntity app = new AppEntity();
        BeanUtil.copyProperties(appVO, app);
        app.setReviewStatus(ReviewStatusEnum.REVIEW.getValue());
        UserVO userVO = userService.getCurrentUser();
        app.setUserId(userVO.getId());
        this.save(app);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteApp(IdVO idVO) {
        Long id = idVO.getId();
        synchronized (this) {
            this.removeById(id);
            boolean removeQuestion = questionService.deleteByAppId(id);
            boolean removeResult = scoringResultService.deleteByAppId(id);
        }
    }

    @Override
    public void updateApp(OperateAppVO appVO) {
        Long id = appVO.getId();
        String appName = appVO.getAppName();
        String appDesc = appVO.getAppDesc();
        String appIcon = appVO.getAppIcon();
        Integer appType = appVO.getAppType();
        Integer scoringStrategy = appVO.getScoringStrategy();

        AppEntity app = this.getById(id);
        ThrowExceptionUtil.throwIf(app == null, ErrorCode.ERROR_PARAM, "应用id错误");

        LambdaUpdateWrapper<AppEntity> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(StrUtil.isNotBlank(appName), AppEntity::getAppName, appName);
        queryWrapper.set(StrUtil.isNotBlank(appDesc), AppEntity::getAppDesc, appDesc);
        queryWrapper.set(StrUtil.isNotBlank(appIcon), AppEntity::getAppIcon, appIcon);
        queryWrapper.set(appType != null, AppEntity::getAppType, appType);
        queryWrapper.set(scoringStrategy != null, AppEntity::getScoringStrategy, scoringStrategy);
        queryWrapper.eq(AppEntity::getId, id);
        this.update(queryWrapper);
    }

    @Override
    public Page<AppVO> selectAppPage(AppPageVO pageVO) {
        String appName = pageVO.getAppName();
        Integer appType = pageVO.getAppType();
        Integer scoringStrategy = pageVO.getScoringStrategy();
        Integer reviewStatus = pageVO.getReviewStatus();
        Integer pageNum = pageVO.getPageNum();
        Integer pageSize = pageVO.getPageSize();
        Boolean isAdmin = pageVO.getIsAdmin();
        Long userId = null;
        if (!isAdmin) {
            UserVO user = userService.getCurrentUser();
            userId = user.getId();
        }
        LambdaQueryWrapper<AppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(appName), AppEntity::getAppName, appName);
        queryWrapper.eq(appType != null, AppEntity::getAppType, appType);
        queryWrapper.eq(scoringStrategy != null, AppEntity::getScoringStrategy, scoringStrategy);
        queryWrapper.eq(reviewStatus != null, AppEntity::getReviewStatus, reviewStatus);
        queryWrapper.eq(userId != null, AppEntity::getUserId, userId);
        queryWrapper.orderByDesc(AppEntity::getCreateTime);
        queryWrapper.orderByDesc(AppEntity::getCreateTime);
        Page<AppEntity> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<AppVO> pageInfo = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AppVO> records = converterVo(page.getRecords());
        pageInfo.setRecords(records);
        return pageInfo;
    }

    @Override
    public List<AppVO> selectAppListByUser(Long userId) {
        LambdaQueryWrapper<AppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppEntity::getUserId, userId);
        queryWrapper.orderByDesc(AppEntity::getCreateTime);
        return converterVo(this.list(queryWrapper));
    }

    @Override
    public boolean reviewApp(ReviewAppVO reviewAppVO) {
        String reviewMessage = reviewAppVO.getReviewMessage();
        Integer reviewStatus = reviewAppVO.getReviewStatus();
        Long appId = reviewAppVO.getAppId();

        AppEntity app = this.getById(appId);
        ThrowExceptionUtil.throwIf(app == null, ErrorCode.ERROR_PARAM, "应用id错误");

        ReviewStatusEnum anEnum = ReviewStatusEnum.getEnumByValue(reviewStatus);
        ThrowExceptionUtil.throwIf(anEnum == null, ErrorCode.ERROR_PARAM, "审核状态错误");

        UserVO user = userService.getCurrentUser();

        LambdaUpdateWrapper<AppEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AppEntity::getReviewStatus, reviewStatus);
        updateWrapper.set(AppEntity::getReviewMessage, reviewMessage);
        updateWrapper.set(AppEntity::getReviewerId, user.getId());
        updateWrapper.set(AppEntity::getReviewTime, new Date());
        updateWrapper.eq(AppEntity::getId, appId);
        return this.update(updateWrapper);
    }

    @Override
    public Map<Long, AppVO> selectAppList(List<Long> appIds) {
        if (CollectionUtil.isEmpty(appIds)) {
            return null;
        }
        List<AppEntity> appEntities = this.listByIds(appIds);
        return converterVo(appEntities).stream().collect(Collectors.toMap(AppVO::getId, Function.identity()));
    }

    private AppVO converterVo(AppEntity app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        return appVO;
    }

    private List<AppVO> converterVo(List<AppEntity> appList) {
        if (CollectionUtil.isEmpty(appList)) {
            return CollectionUtil.newArrayList();
        }
        List<Long> createUserIds = appList.stream().map(AppEntity::getUserId).distinct().collect(Collectors.toList());
        List<Long> reviewUserIds = appList.stream().map(AppEntity::getReviewerId).distinct().collect(Collectors.toList());
        createUserIds.addAll(reviewUserIds);
        List<UserVO> userVOList = userService.selectUserInfoBatch(createUserIds);
        List<AppVO> appVOList = appList.stream().map(this::converterVo).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(userVOList)) {
            Map<Long, UserVO> userMap = userVOList.stream().collect(Collectors.toMap(UserVO::getId, Function.identity()));
            appVOList.stream().map(app -> {
                app.setUser(userMap.get(app.getUserId()));
                app.setReviewUser(userMap.get(app.getReviewerId()));
                return app;
            }).collect(Collectors.toList());
        }
        return appVOList;
    }

    private void checkParams(OperateAppVO appVO) {
        String appName = appVO.getAppName();
        Integer appType = appVO.getAppType();
        Integer scoringStrategy = appVO.getScoringStrategy();
        ThrowExceptionUtil.throwIf(StrUtil.isBlank(appName), ErrorCode.ERROR_PARAM, "应用名称不能为空");
        ThrowExceptionUtil.throwIf(appType == null, ErrorCode.ERROR_PARAM, "应用类型不能为空");
        ThrowExceptionUtil.throwIf(scoringStrategy == null, ErrorCode.ERROR_PARAM, "评分策略不能为空");

    }
}
