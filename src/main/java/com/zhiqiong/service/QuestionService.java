package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.QuestionEntity;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.QuestionPageVO;
import com.zhiqiong.model.vo.question.TopicVO;

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

    boolean deleteByAppId(Long appId);

    Page<TopicVO> selectTopicPage(QuestionPageVO questionPageVO);
}
