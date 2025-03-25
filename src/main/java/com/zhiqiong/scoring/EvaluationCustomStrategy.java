package com.zhiqiong.scoring;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.MBTIResult;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.scoring.model.ScoringResult;
import com.zhiqiong.utils.ThrowExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测评类 + 自定义评分（1-0）
 *
 * @author huangkun
 * @date 2025/3/25 17:24
 */
@ScoringStrategyAnnotation(appType = 1, scoringStrategy = 0)
@Slf4j
public class EvaluationCustomStrategy implements ScoringStrategy {
    @Override
    public ScoringResult calculateScore(List<TopicVO> topicVOList, List<String> choices) {
        HashMap<String, Integer> typeCount = new HashMap<>();
        int countScore = 0;
        for (int i = 0; i < topicVOList.size(); i++) {
            TopicVO topicVO = topicVOList.get(i);
            String answer = choices.get(i);
            List<OptionVO> optionVOList = topicVO.getOptions();
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
        return ScoringResult.builder()
                .resultProp(typeCode)
                .resultScoreRange(countScore)
                .score(countScore)
                .typeName(typeName)
                .description(description)
                .build();
    }
}
