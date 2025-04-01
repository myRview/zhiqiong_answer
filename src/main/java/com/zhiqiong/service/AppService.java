package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.app.AppPageVO;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.app.OperateAppVO;
import com.zhiqiong.model.vo.app.ReviewAppVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用 服务类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
public interface AppService extends IService<AppEntity> {


    AppVO selectAppInfo(Long id);

    List<AppVO> selectAppList(String appName, Integer reviewStatus);

    void addApp(OperateAppVO appVO);

    void deleteApp(IdVO idVO);

    void updateApp(OperateAppVO appVO);

    List<AppVO> selectAppListByUser(Long userId);

    boolean reviewApp(ReviewAppVO reviewAppVO);

    Page<AppVO> selectAppPage(AppPageVO pageVO);

    Map<Long, AppVO> selectAppList(List<Long> appIds);
}
