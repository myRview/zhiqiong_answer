package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.ScoringResultEntity;
import com.zhiqiong.model.vo.score.ScoreResultPageVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评分结果 服务类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
public interface ScoringResultService extends IService<ScoringResultEntity> {

    boolean addScoreResult(ScoringResultVO resultVO);

    Page<ScoringResultVO> selectScoreResultPage(ScoreResultPageVO pageVO);

    boolean updateScoreResult(ScoringResultVO resultVO);

    Map<Long, List<ScoringResultVO>> selectMapByAll();

    ScoringResultVO selectByAppIdAndTypeCode(Long appId, String typeCode);

    boolean deleteByAppId(Long appId);
}
