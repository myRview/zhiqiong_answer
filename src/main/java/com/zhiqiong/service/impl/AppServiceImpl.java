package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.enums.ReviewStatusEnum;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.mapper.AppMapper;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.AppService;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.JwtUtil;
import com.zhiqiong.utils.ThrowExceptionUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
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
        return converterVo(this.list(queryWrapper));
    }

    @Override
    public void addApp(AppVO appVO) {
        AppEntity app = new AppEntity();
        BeanUtil.copyProperties(appVO, app);
        app.setReviewStatus(ReviewStatusEnum.REVIEW.getValue());
        UserVO userVO = userService.getCurrentUser();
        app.setUserId(userVO.getId());
        this.save(app);
    }

    @Override
    public void deleteApp(IdVO idVO) {
        this.removeById(idVO.getId());
    }

    @Override
    public void updateApp(AppVO appVO) {
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
    public List<AppVO> selectAppListByUser(Long userId) {
        LambdaQueryWrapper<AppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppEntity::getUserId, userId);
        return converterVo(this.list(queryWrapper));
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
        return appList.stream().map(this::converterVo).collect(Collectors.toList());
    }
}
