package com.zhiqiong.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.model.entity.AppEntity;
import com.zhiqiong.mapper.AppMapper;
import com.zhiqiong.model.entity.UserEntity;
import com.zhiqiong.model.vo.RegisterUserVO;
import com.zhiqiong.service.AppService;
import com.zhiqiong.utils.ThrowExceptionUtil;
import org.springframework.stereotype.Service;

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

}
