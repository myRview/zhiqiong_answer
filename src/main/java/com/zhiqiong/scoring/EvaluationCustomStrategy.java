package com.zhiqiong.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.cache.RedisCacheManager;
import com.zhiqiong.common.constant.CacheConstants;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;
import com.zhiqiong.service.ScoringResultService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 测评类 + 自定义评分（1-0）
 *
 * @author huangkun
 * @date 2025/3/25 17:24
 */
@ScoringStrategyAnnotation(appType = 1, scoringStrategy = 0)
@Slf4j
public class EvaluationCustomStrategy implements ScoringStrategy {

    @Resource
    private RedisCacheManager redisCacheManager;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public ScoringResultVO calculateScore(List<TopicVO> topicVOList, List<String> choices, AppVO app) {
        //TODO:功能需要修改
        HashMap<String, Integer> typeCount = new HashMap<>();
        for (int i = 0; i < topicVOList.size(); i++) {
            TopicVO topicVO = topicVOList.get(i);
            String answer = choices.get(i);
            List<OptionVO> optionVOList = topicVO.getOptions();
            for (OptionVO optionVO : optionVOList) {
                if (StrUtil.equals(optionVO.getKey(), answer)) {
                    String result = optionVO.getResult();
                    typeCount.merge(result, 1, Integer::sum);
                    break;
                }
            }
        }
        int eCount = typeCount.getOrDefault("E", 0);
        int iCount = typeCount.getOrDefault("I", 0);
        int sCount = typeCount.getOrDefault("S", 0);
        int nCount = typeCount.getOrDefault("N", 0);
        int tCount = typeCount.getOrDefault("T", 0);
        int fCount = typeCount.getOrDefault("F", 0);
        int jCount = typeCount.getOrDefault("J", 0);
        int pCount = typeCount.getOrDefault("P", 0);

        // 使用StringBuilder拼接结果
        String typeCode = new StringBuilder()
                .append(eCount >= iCount ? "E" : "I")
                .append(sCount >= nCount ? "S" : "N")
                .append(tCount >= fCount ? "T" : "F")
                .append(jCount >= pCount ? "J" : "P")
                .toString();

        //根据用户答题结果，查询得分结果
        Long appId = app.getId();
        String cacheMapValue = redisCacheManager.getCacheMapValue(CacheConstants.SCORE_RESULT_KEY + appId, typeCode);
        ScoringResultVO scoringResultVO = null;
        if (StrUtil.isNotBlank(cacheMapValue)) {
            scoringResultVO = JSONUtil.toBean(cacheMapValue, ScoringResultVO.class);
        }else {
            scoringResultVO = scoringResultService.selectByAppIdAndTypeCode(appId, typeCode);
        }
        return scoringResultVO;
    }
}
