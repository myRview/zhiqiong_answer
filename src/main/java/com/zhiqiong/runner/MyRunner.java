package com.zhiqiong.runner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.zhiqiong.cache.RedisCacheManager;
import com.zhiqiong.common.constant.CacheConstants;
import com.zhiqiong.model.vo.score.ScoringResultVO;
import com.zhiqiong.service.ScoringResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author huangkun
 * @date 2025/4/2 14:56
 */
@Component
@Slf4j
public class MyRunner implements ApplicationRunner {
    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private RedisCacheManager redisCacheManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<Long, List<ScoringResultVO>> resultMap = scoringResultService.selectMapByAll();
        if (CollectionUtil.isEmpty(resultMap)) return;
        for (Long appId : resultMap.keySet()) {
            List<ScoringResultVO> scoringResultVOS = resultMap.get(appId);
            if (CollectionUtil.isEmpty(scoringResultVOS)) continue;
            Map<String, String> cacheMap = new HashMap<>(scoringResultVOS.size());
            for (ScoringResultVO resultVO : scoringResultVOS) {
                cacheMap.put(resultVO.getResultProp(), JSONUtil.toJsonStr(resultVO));
            }
            try {
                // 批量写入Redis
                String cacheKey = CacheConstants.SCORE_RESULT_KEY + appId;
                redisCacheManager.setCacheMap(cacheKey, cacheMap);
                redisCacheManager.expire(cacheKey, 30, TimeUnit.DAYS);
            } catch (Exception e) {
                log.error("批量写入Redis失败，数据：{}", cacheMap);
            }
        }
    }
}
