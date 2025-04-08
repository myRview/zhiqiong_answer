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

        double totalScore = 0;
        for (int i = 0; i < topicVOList.size(); i++) {
            TopicVO topic = topicVOList.get(i);
            String choice = choices.get(i);
            List<OptionVO> options = topic.getOptions();
            for (OptionVO option : options) {
                if (StrUtil.equals(option.getKey(), choice)) {
                    String result = option.getResult();
                    Double value = Double.valueOf(result);
                    totalScore += value;
                    break;
                }
            }
        }
        //计算当前分数的整数位
        int score = (int) totalScore / 10 * 10;
        String typeCode = String.valueOf(score);
        // 解析维度配置并生成类型代码
        Long appId = app.getId();
        String cacheKey = CacheConstants.SCORE_RESULT_KEY + appId;
        String cacheMapValue = redisCacheManager.getCacheMapValue(cacheKey, typeCode);
        ScoringResultVO scoringResultVO = null;
        if (StrUtil.isNotBlank(cacheMapValue)) {
            scoringResultVO = JSONUtil.toBean(cacheMapValue, ScoringResultVO.class);
        } else {
            scoringResultVO = scoringResultService.selectByAppIdAndTypeCode(appId, typeCode);
        }
        if (scoringResultVO == null) {
            scoringResultVO = new ScoringResultVO();
            scoringResultVO.setResultScoreRange(score);
        }
        return scoringResultVO;
    }
}
