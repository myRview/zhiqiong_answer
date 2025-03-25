package com.zhiqiong.scoring;

import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.scoring.model.ScoringResult;

import java.util.List;

/**
 * 测评类 + AI评分（1-1）
 *
 * @author huangkun
 * @date 2025/3/25 17:24
 */
@ScoringStrategyAnnotation(appType = 1, scoringStrategy = 1)
public class EvaluationAIStrategy implements ScoringStrategy {
    @Override
    public ScoringResult calculateScore(List<TopicVO> topics, List<String> choices) {
        return null;
    }
}
