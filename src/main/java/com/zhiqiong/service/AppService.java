package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.app.AppVO;

import java.util.List;

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

    void addApp(AppVO appVO);

    void deleteApp(IdVO idVO);

    void updateApp(AppVO appVO);

    List<AppVO> selectAppListByUser(Long userId);
}
