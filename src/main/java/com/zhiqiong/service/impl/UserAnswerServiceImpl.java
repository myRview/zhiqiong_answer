package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.MBTIResult;
import com.zhiqiong.model.entity.ScoringResultEntity;
import com.zhiqiong.model.entity.UserAnswerEntity;
import com.zhiqiong.mapper.UserAnswerMapper;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.question.UserAnswerVO;
import com.zhiqiong.model.vo.user.UserVO;
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


    @Override
    public UserAnswerVO selectAnswerListByApp(Long appId, Long userId) {
        LambdaQueryWrapper<UserAnswerEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAnswerEntity::getAppId, appId);
        queryWrapper.eq(ObjUtil.isNotNull(userId), UserAnswerEntity::getUserId, userId);
        UserAnswerEntity userAnswerEntity = this.getOne(queryWrapper);
        return converterVO(userAnswerEntity);
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
        //统计用户每种类型的数量
        HashMap<String, Integer> typeCount = new HashMap<>();
        for (int i = 0; i < topicVOList.size(); i++) {
            TopicVO topicVO = topicVOList.get(i);
            String answer = choicesResult.get(i);
            List<OptionVO> optionVOList = topicVO.getOptionVOList();
            for (OptionVO optionVO : optionVOList) {
                if (StrUtil.equals(optionVO.getKey(), answer)) {
                    String result = optionVO.getResult();
                    Integer count = typeCount.getOrDefault(result, 0);
                    typeCount.put(result, count + 1);
                    break;
                }
            }
        }
        String typeCode = (typeCount.getOrDefault("E", 0) >= typeCount.getOrDefault("I", 0) ? "E" : "I") +
                (typeCount.getOrDefault("S", 0) >= typeCount.getOrDefault("N", 0) ? "S" : "N") +
                (typeCount.getOrDefault("T", 0) >= typeCount.getOrDefault("F", 0) ? "T" : "F") +
                (typeCount.getOrDefault("J", 0) >= typeCount.getOrDefault("P", 0) ? "J" : "P");

        //根据用户答题结果，查询得分结果
        Map<String, Integer> dimensionScores = MBTIResult.getDimensionScores();
        int countScore = 0;
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Integer score = dimensionScores.getOrDefault(key, 0);
            countScore = score * value;
        }
        MBTIResult mbtiResult = MBTIResult.getResult(typeCode);
        ThrowExceptionUtil.throwIf(ObjUtil.isNull(mbtiResult), ErrorCode.ERROR_PARAM, "没有获取到MBTI类型");

        String typeName = mbtiResult.getTypeName();
        String description = mbtiResult.getDescription();

        ScoringResultEntity score = new ScoringResultEntity();
        score.setResultName(typeName);
        score.setResultDesc(description);
//        score.setResultPicture();
//        score.setResultProp();
//        score.setResultScoreRange();
        score.setAppId(appId);
        score.setUserId(user.getId());
        scoringResultService.save(score);

        UserAnswerEntity userAnswerEntity = new UserAnswerEntity();
        userAnswerEntity.setAppId(appId);
        userAnswerEntity.setAppType(appVO.getAppType());
        userAnswerEntity.setScoringStrategy(appVO.getScoringStrategy());
        userAnswerEntity.setChoices(JSONUtil.toJsonStr(choicesResult));
        userAnswerEntity.setUserId(user.getId());
        userAnswerEntity.setResultId(score.getId());
        userAnswerEntity.setResultName(typeName);
        userAnswerEntity.setResultDesc(description);
//        userAnswerEntity.setResultPicture();
        userAnswerEntity.setResultScore(countScore);
        this.save(userAnswerEntity);
    }


    private UserAnswerVO converterVO(UserAnswerEntity userAnswerEntity) {
        if (userAnswerEntity == null) return null;
        UserAnswerVO userAnswerVO = new UserAnswerVO();
        BeanUtil.copyProperties(userAnswerEntity, userAnswerVO);
        userAnswerVO.setChoicesResult(JSONUtil.toList(userAnswerEntity.getChoices(), String.class));
        return userAnswerVO;
    }
}
