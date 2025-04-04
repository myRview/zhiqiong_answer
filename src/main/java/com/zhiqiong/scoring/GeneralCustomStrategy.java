package com.zhiqiong.scoring;

import cn.hutool.core.util.NumberUtil;
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
import java.util.concurrent.TimeUnit;

/**
 * 得分类 + 自定义评分（0-0）
 *
 * @author huangkun
 * @date 2025/3/25 17:21
 */
@ScoringStrategyAnnotation(appType = 0, scoringStrategy = 0)
@Slf4j
public class GeneralCustomStrategy implements ScoringStrategy {


    @Resource
    private RedisCacheManager redisCacheManager;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public ScoringResultVO calculateScore(List<TopicVO> topicVOList, List<String> choices, AppVO app) {
        int size = topicVOList.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            TopicVO topicVO = topicVOList.get(i);
            String answer = choices.get(i);
            List<OptionVO> optionVOList = topicVO.getOptions();
            boolean present = optionVOList.stream().anyMatch(option -> StrUtil.equals(option.getResult(), "true") && StrUtil.equals(option.getKey(), answer));
            if (present) {
                count++;
            }
        }
        Long appId = app.getId();
        double aDouble = NumberUtil.mul(100, NumberUtil.div(count, size, 2));
        //获取当前分值位于哪个区间
        int x = (int) aDouble / 10 * 10;

        String parentKey = CacheConstants.SCORE_RESULT_KEY + appId;
        String key = String.valueOf(x);
        String cacheMapValue = redisCacheManager.getCacheMapValue(parentKey, key);
        ScoringResultVO scoringResultVO = null;
        if (StrUtil.isNotBlank(cacheMapValue)) {
            scoringResultVO = JSONUtil.toBean(cacheMapValue, ScoringResultVO.class);
            return scoringResultVO;
        }
        scoringResultVO = scoringResultService.selectByAppIdAndTypeCode(appId, key);
        if (scoringResultVO == null) {
            scoringResultVO = new ScoringResultVO();
            scoringResultVO.setResultName("");
            scoringResultVO.setResultDesc("");
            scoringResultVO.setResultProp("");
            scoringResultVO.setResultScoreRange(x);
            scoringResultVO.setAppId(appId);
        } else {
            scoringResultVO.setCreateTime(null);
            scoringResultVO.setUserId(null);
            scoringResultVO.setId(null);
            scoringResultVO.setAppId(null);
            redisCacheManager.setCacheMapValue(parentKey, key, JSONUtil.toJsonStr(scoringResultVO));
            redisCacheManager.expire(key, 30, TimeUnit.DAYS);
        }
        return scoringResultVO;
    }

    public static void main(String[] args) {
        double aDouble = NumberUtil.mul(100, NumberUtil.div(8, 19, 2));
        int x = (int) aDouble / 10 * 10;
        System.out.println(x);
    }

}
