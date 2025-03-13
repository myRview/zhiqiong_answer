package com.zhiqiong.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.model.entity.UserAnswerEntity;
import com.zhiqiong.mapper.UserAnswerMapper;
import com.zhiqiong.service.UserAnswerService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户答题记录 服务实现类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Service
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswerEntity> implements UserAnswerService {

}
