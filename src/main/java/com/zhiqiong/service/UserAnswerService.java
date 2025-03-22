package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.UserAnswerEntity;
import com.zhiqiong.model.vo.question.UserAnswerVO;

/**
 * <p>
 * 用户答题记录 服务类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
public interface UserAnswerService extends IService<UserAnswerEntity> {

    UserAnswerVO selectAnswerListByApp(Long appId, Long userId);

    void submitAnswer(UserAnswerVO userAnswerVO);

}
