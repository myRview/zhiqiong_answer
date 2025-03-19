package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.QuestionEntity;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.QuestionVO;
import com.zhiqiong.model.vo.question.TopicVO;

import java.util.List;

/**
 * <p>
 * 题目 服务类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
public interface QuestionService extends IService<QuestionEntity> {


    AddQuestionVO selectQuestionList(Long appId);

    boolean addQuestion(AddQuestionVO addQuestionVO);


    TopicVO selectTopicInfo(Long id);

    boolean updateTopic(TopicVO topicVO);
}
