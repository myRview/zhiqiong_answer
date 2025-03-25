package com.zhiqiong.scoring;

import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.scoring.model.ScoringResult;

import java.util.List;

/**
 * @author huangkun
 * @date 2025/3/25 17:02
 */
public interface ScoringStrategy {
    ScoringResult calculateScore(List<TopicVO> topics, List<String> choices);
}
