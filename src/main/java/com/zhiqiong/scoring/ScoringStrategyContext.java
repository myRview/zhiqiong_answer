package com.zhiqiong.scoring;

import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.scoring.model.ScoringResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author 20231
 */
@Component
@Slf4j
public class ScoringStrategyContext {
    @Resource
    private List<ScoringStrategy> strategies;

    public ScoringResult calculateScore(AppVO appVO, List<String> choicesResult,List<TopicVO> topicVOList) {
        Integer appType = appVO.getAppType();
        Integer scoringStrategy = appVO.getScoringStrategy();
        for (ScoringStrategy strategy : strategies) {
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyAnnotation.class)) {
                ScoringStrategyAnnotation annotation = strategy.getClass().getAnnotation(ScoringStrategyAnnotation.class);
                if (appType.equals(annotation.appType()) && scoringStrategy.equals(annotation.scoringStrategy())) {
                    return strategy.calculateScore(topicVOList, choicesResult);
                }
            }
        }
        throw new RuntimeException("未找到对应的策略");
    }
}