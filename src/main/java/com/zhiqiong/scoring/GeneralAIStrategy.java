package com.zhiqiong.scoring;

import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.scoring.model.ScoringResult;

import java.util.List;

/**
 * 得分类 + AI评分（0-1）
 *
 * @author huangkun
 * @date 2025/3/25 17:23
 */
@ScoringStrategyAnnotation(appType = 0, scoringStrategy = 1)
public class GeneralAIStrategy implements ScoringStrategy {
    @Override
    public ScoringResult calculateScore(List<TopicVO> topics, List<String> choices) {
        return null;
    }
}
