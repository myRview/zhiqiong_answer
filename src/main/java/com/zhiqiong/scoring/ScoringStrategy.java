package com.zhiqiong.scoring;

import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;

import java.util.List;

/**
 * @author huangkun
 * @date 2025/3/25 17:02
 */
public interface ScoringStrategy {
    ScoringResultVO calculateScore(List<TopicVO> topics, List<String> choices, AppVO app);
}
