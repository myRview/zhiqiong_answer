package com.zhiqiong.scoring;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.scoring.model.ScoringResult;

import java.util.List;

/**
 * 得分类 + 自定义评分（0-0）
 *
 * @author huangkun
 * @date 2025/3/25 17:21
 */
@ScoringStrategyAnnotation(appType = 0, scoringStrategy = 0)
public class GeneralCustomStrategy implements ScoringStrategy {
    @Override
    public ScoringResult calculateScore(List<TopicVO> topicVOList, List<String> choices) {
        int size = topicVOList.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            TopicVO topicVO = topicVOList.get(i);
            String answer = choices.get(i);
            List<OptionVO> optionVOList = topicVO.getOptions();
            boolean present = optionVOList.stream().anyMatch(option -> StrUtil.equals(option.getResult(), "true") &&
                    StrUtil.equals(option.getKey(), answer));
            if (present) {
                count++;
            }
        }
        Double aDouble = NumberUtil.mul(100, NumberUtil.div(count, size, 2));
        return ScoringResult.builder()
                .score(aDouble.intValue())
                .typeName("")
                .description("")
                .build();
    }
}
