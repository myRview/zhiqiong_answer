package com.zhiqiong.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.model.entity.QuestionEntity;
import com.zhiqiong.mapper.QuestionMapper;
import com.zhiqiong.service.QuestionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 题目 服务实现类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionEntity> implements QuestionService {

}
