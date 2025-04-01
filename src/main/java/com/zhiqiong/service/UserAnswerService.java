package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.UserAnswerEntity;
import com.zhiqiong.model.vo.answwer.AnswerPageVO;
import com.zhiqiong.model.vo.answwer.UserAnswerVO;

import java.util.List;

/**
 * <p>
 * 用户答题记录 服务类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
public interface UserAnswerService extends IService<UserAnswerEntity> {

    UserAnswerVO selectAnswerById(Long id);

    List<UserAnswerVO> selectAnswerListByApp(Long appId, Long userId);

    UserAnswerVO submitAnswer(UserAnswerVO userAnswerVO);

    Page<UserAnswerVO> selectAnswerPage(AnswerPageVO answerPageVO);
}
