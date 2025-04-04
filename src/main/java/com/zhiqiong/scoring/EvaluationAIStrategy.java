package com.zhiqiong.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.annotation.ScoringStrategyAnnotation;
import com.zhiqiong.cache.RedisCacheManager;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.constant.CacheConstants;
import com.zhiqiong.exception.BusinessException;
import com.zhiqiong.manager.cos.CosManager;
import com.zhiqiong.manager.oapi.AIService;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测评类 + AI评分（1-1）
 *
 * @author huangkun
 * @date 2025/3/25 17:24
 */
@ScoringStrategyAnnotation(appType = 1, scoringStrategy = 1)
@Slf4j
public class EvaluationAIStrategy implements ScoringStrategy {

    @Resource
    private AIService aiService;
    @Resource
    private RedisCacheManager redisCacheManager;
    @Resource
    private RedissonClient redissonClient;


    @Override
    public ScoringResultVO calculateScore(List<TopicVO> topics, List<String> choices, AppVO appVO) {
        String key = appVO.getId().toString() + JSONUtil.toJsonStr(choices);
        //进行MD5压缩
        String md5Hex = DigestUtil.md5Hex(key);

        String cacheKey = CacheConstants.SCORE_RESULT_KEY + appVO.getId();
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
            String result = aiService.aiGeneratorResult(topics, choices, appVO);
            int indexOf = result.indexOf("{");
            int lastIndexOf = result.lastIndexOf("}");
            String json = result.substring(indexOf, lastIndexOf + 1);
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
}
