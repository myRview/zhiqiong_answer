package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.MBTIResult;
import com.zhiqiong.enums.AppTypeEnum;
import com.zhiqiong.mapper.UserAnswerMapper;
import com.zhiqiong.model.entity.ScoringResultEntity;
import com.zhiqiong.model.entity.UserAnswerEntity;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.question.UserAnswerVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.scoring.ScoringStrategyContext;
import com.zhiqiong.scoring.model.ScoringResult;
import com.zhiqiong.service.*;
import com.zhiqiong.utils.ThrowExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户答题记录 服务实现类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Service
@Slf4j
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswerEntity> implements UserAnswerService {

    @Resource
    private AppService appService;
    @Resource
    private UserService userService;
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private ScoringStrategyContext scoringStrategyContext;



    @Override
    public List<UserAnswerVO> selectAnswerListByApp(Long appId, Long userId) {
        LambdaQueryWrapper<UserAnswerEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAnswerEntity::getAppId, appId);
        queryWrapper.eq(ObjUtil.isNotNull(userId), UserAnswerEntity::getUserId, userId);
        queryWrapper.orderByDesc(UserAnswerEntity::getId);
        List<UserAnswerEntity> list = this.list(queryWrapper);
        return converterVO(list);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitAnswer(UserAnswerVO userAnswerVO) {
        Long appId = userAnswerVO.getAppId();
        AppVO appVO = appService.selectAppInfo(appId);
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(appVO), ErrorCode.ERROR_PARAM, "应用不存在");

        UserVO user = userService.getCurrentUser();
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(user), ErrorCode.ERROR_PARAM, "应用不存在");

        List<String> choicesResult = userAnswerVO.getChoicesResult();
        log.error("用户答案：{}", choicesResult);
        ThrowExceptionUtil.throwIf(CollectionUtil.isEmpty(choicesResult), ErrorCode.ERROR_PARAM, "请选择答案");

        AddQuestionVO questionVO = questionService.selectQuestionList(appId);
        List<TopicVO> topicVOList = questionVO.getTopicVOList();
        ThrowExceptionUtil.throwIf(CollectionUtil.isEmpty(topicVOList), ErrorCode.ERROR_PARAM, "应用暂无考题");


        //计算用户答题结果
        ScoringResult result = scoringStrategyContext.calculateScore(appVO, choicesResult, topicVOList);
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(result), ErrorCode.ERROR_PARAM, "计算结果异常");

//        String typeName = result.getTypeName();
//        String description = result.getDescription();
//        int resultScore = result.getScore();
//        ScoringResultEntity score = new ScoringResultEntity();
//        score.setResultName(typeName);
//        score.setResultDesc(description);
//        score.setResultPicture(result.getResultPicture());
//        score.setResultProp(result.getResultProp());
//        score.setResultScoreRange(result.getResultScoreRange());
//        score.setAppId(appId);
//        score.setUserId(user.getId());
//        scoringResultService.save(score);
//
//        UserAnswerEntity userAnswerEntity = new UserAnswerEntity();
//        userAnswerEntity.setAppId(appId);
//        userAnswerEntity.setAppType(appVO.getAppType());
//        userAnswerEntity.setScoringStrategy(appVO.getScoringStrategy());
//        userAnswerEntity.setChoices(JSONUtil.toJsonStr(choicesResult));
//        userAnswerEntity.setUserId(user.getId());
//        userAnswerEntity.setResultId(score.getId());
//        userAnswerEntity.setResultName(typeName);
//        userAnswerEntity.setResultDesc(description);
//        userAnswerEntity.setResultPicture(result.getResultPicture());
//        userAnswerEntity.setResultScore(resultScore);
//        this.save(userAnswerEntity);
    }


    private UserAnswerVO converterVO(UserAnswerEntity userAnswerEntity) {
        if (userAnswerEntity == null) return null;
        UserAnswerVO userAnswerVO = new UserAnswerVO();
        BeanUtil.copyProperties(userAnswerEntity, userAnswerVO);
        userAnswerVO.setChoicesResult(JSONUtil.toList(userAnswerEntity.getChoices(), String.class));
        return userAnswerVO;
    }

    private List<UserAnswerVO> converterVO(List<UserAnswerEntity> userAnswerList) {
        if (CollectionUtil.isEmpty(userAnswerList)) return null;
        return userAnswerList.stream().map(this::converterVO).collect(Collectors.toList());
    }
}
