package com.zhiqiong.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.mapper.QuestionMapper;
import com.zhiqiong.model.entity.QuestionEntity;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.AppService;
import com.zhiqiong.service.QuestionService;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.ThrowExceptionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private AppService appService;
    @Resource
    private UserService userService;


    @Override
    public TopicVO selectTopicInfo(Long id) {
        QuestionEntity question = this.getById(id);
        return converterVO(question);
    }

    @Override
    public AddQuestionVO selectQuestionList(Long appId) {
        LambdaQueryWrapper<QuestionEntity> query = new LambdaQueryWrapper<>();
        query.eq(QuestionEntity::getAppId, appId);
        List<QuestionEntity> questionEntities = this.baseMapper.selectList(query);
        AddQuestionVO addQuestionVO = new AddQuestionVO();
        addQuestionVO.setTopicVOList(converterVO(questionEntities));
        addQuestionVO.setAppId(appId);
        return addQuestionVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addQuestion(AddQuestionVO addQuestionVO) {
        List<TopicVO> topicVOList = addQuestionVO.getTopicVOList();
        Long appId = addQuestionVO.getAppId();
        ThrowExceptionUtil.throwIf(CollectionUtil.isEmpty(topicVOList), ErrorCode.ERROR_PARAM, "题目列表不能为空");
        AppVO appVO = appService.selectAppInfo(appId);
        ThrowExceptionUtil.throwIf(appVO == null, ErrorCode.ERROR_PARAM, "应用id错误");

        UserVO user = userService.getCurrentUser();
        ThrowExceptionUtil.throwIf(user == null, ErrorCode.ERROR_PARAM, "用户未登录");

        List<QuestionEntity> questionEntities = new ArrayList<>(topicVOList.size());
        for (TopicVO topicVO : topicVOList) {
            QuestionEntity questionVO = new QuestionEntity();
            String jsonStr = JSONUtil.toJsonStr(topicVO);
            questionVO.setQuestionContent(jsonStr);
            questionVO.setAppId(appId);
            questionVO.setUserId(user.getId());
            questionEntities.add(questionVO);
        }
        return this.saveBatch(questionEntities);
    }

    @Override
    public boolean updateTopic(TopicVO topicVO) {
        Long id = topicVO.getId();
        QuestionEntity question = this.getById(id);
        ThrowExceptionUtil.throwIf(question == null, ErrorCode.ERROR_PARAM, "题目id错误");
        String jsonStr = JSONUtil.toJsonStr(topicVO);
        return this.lambdaUpdate()
                .set(QuestionEntity::getQuestionContent, jsonStr)
                .eq(QuestionEntity::getId, id)
                .update();
    }

    private List<TopicVO> converterVO(List<QuestionEntity> questionEntities) {
        if (CollectionUtil.isEmpty(questionEntities)) return CollectionUtil.newArrayList();
        return questionEntities.stream().map(this::converterVO).collect(Collectors.toList());
    }

    private TopicVO converterVO(QuestionEntity questionEntity) {
        if (questionEntity == null) return null;
        String jsonStr = questionEntity.getQuestionContent();
        TopicVO topicVO = JSONUtil.toBean(jsonStr, TopicVO.class);
        topicVO.setId(questionEntity.getId());
        return topicVO;
    }
}
