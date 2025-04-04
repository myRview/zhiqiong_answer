package com.zhiqiong.scoring;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.cache.RedisCacheManager;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.constant.CacheConstants;
import com.zhiqiong.exception.BusinessException;
import com.zhiqiong.manager.oapi.AIService;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.OptionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 得分类 + AI评分（0-1）
 *
 * @author huangkun
 * @date 2025/3/25 17:23
 */
@ScoringStrategyAnnotation(appType = 0, scoringStrategy = 1)
public class GeneralAIStrategy implements ScoringStrategy {

    @Resource
    private AIService aiService;
    @Resource
    private RedisCacheManager redisCacheManager;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public ScoringResultVO calculateScore(List<TopicVO> topics, List<String> choices, AppVO app) {
        String key = app.getId() + JSONUtil.toJsonStr(choices);
        //进行MD5压缩
        String md5Hex = DigestUtil.md5Hex(key);

        String cacheKey = CacheConstants.SCORE_RESULT_KEY + app.getId();
        String cacheMapValue = redisCacheManager.getCacheMapValue(cacheKey, md5Hex);
        ScoringResultVO resultVO = null;
        if (StrUtil.isNotBlank(cacheMapValue)) {
            resultVO = JSONUtil.toBean(cacheMapValue, ScoringResultVO.class);
            return resultVO;
        }
        //获取锁
        String lockKey = CacheConstants.AI_ANSWER_LOCK + md5Hex;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(3, 15, TimeUnit.SECONDS);
            String result = aiService.aiGeneratorResult(topics, choices, app);
            int indexOf = result.indexOf("{");
            int lastIndexOf = result.lastIndexOf("}");
            String json = result.substring(indexOf, lastIndexOf + 1);
            int score = computeScore(topics, choices);
            resultVO.setResultScoreRange(score);
            resultVO = JSONUtil.toBean(json, ScoringResultVO.class);
            redisCacheManager.setCacheMapValue(cacheKey, md5Hex, JSONUtil.toJsonStr(resultVO));
            redisCacheManager.expire(cacheKey, 60 * 60 * 24);
            return resultVO;
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.ERROR_SYSTEM);
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    public int computeScore(List<TopicVO> topics, List<String> choices) {
        if (CollectionUtil.isEmpty(topics) || CollectionUtil.isEmpty(choices)) return 0;
        int size = topics.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            TopicVO topicVO = topics.get(i);
            String answer = choices.get(i);
            List<OptionVO> optionVOList = topicVO.getOptions();
            boolean present = optionVOList.stream().anyMatch(option -> StrUtil.equals(option.getResult(), "true") &&
                    StrUtil.equals(option.getKey(), answer));
            if (present) {
                count++;
            }
        }
        double aDouble = NumberUtil.mul(100, NumberUtil.div(count, size, 2));
        return (int) aDouble;
    }
}
