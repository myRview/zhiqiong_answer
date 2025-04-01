package com.zhiqiong.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhiqiong.model.entity.ScoringResultEntity;
import com.zhiqiong.model.vo.score.ScoreResultPageVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;

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
}
