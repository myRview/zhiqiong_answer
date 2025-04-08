package com.zhiqiong.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.model.entity.ScoringResultEntity;
import com.zhiqiong.mapper.ScoringResultMapper;
import com.zhiqiong.model.vo.score.ScoreResultPageVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.ScoringResultService;
import com.zhiqiong.service.UserService;
import com.zhiqiong.utils.ThrowExceptionUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 评分结果 服务实现类
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@Service
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResultEntity> implements ScoringResultService {


    @Resource
    private UserService userService;

    @Override
    public boolean addScoreResult(ScoringResultVO resultVO) {
        String resultName = resultVO.getResultName();
        String resultDesc = resultVO.getResultDesc();
        String resultPicture = resultVO.getResultPicture();
        String resultProp = resultVO.getResultProp();
        Integer resultScoreRange = resultVO.getResultScoreRange();
        Long appId = resultVO.getAppId();


        UserVO user = userService.getCurrentUser();
        ScoringResultEntity entity = new ScoringResultEntity();
        entity.setResultName(resultName);
        entity.setResultDesc(resultDesc);
        entity.setResultPicture(resultPicture);
        entity.setResultProp(resultProp);
        entity.setResultScoreRange(resultScoreRange);
        entity.setAppId(appId);
        entity.setUserId(user.getId());
        return this.save(entity);
    }

    @Override
    public Page<ScoringResultVO> selectScoreResultPage(ScoreResultPageVO pageVO) {
        String resultName = pageVO.getResultName();
        Long appId = pageVO.getAppId();
        Integer pageNum = pageVO.getPageNum();
        Integer pageSize = pageVO.getPageSize();
        LambdaQueryWrapper<ScoringResultEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(resultName), ScoringResultEntity::getResultName, resultName);
        queryWrapper.eq(appId != null, ScoringResultEntity::getAppId, appId);
        Page<ScoringResultEntity> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<ScoringResultVO> resultPage = new Page<>(pageNum, pageSize, page.getTotal());
        resultPage.setRecords(converterVO(page.getRecords()));
        return resultPage;
    }


    @Override
    public boolean updateScoreResult(ScoringResultVO resultVO) {
        Long id = resultVO.getId();
        String resultName = resultVO.getResultName();
        String resultDesc = resultVO.getResultDesc();
        String resultPicture = resultVO.getResultPicture();
        String resultProp = resultVO.getResultProp();
        Integer resultScoreRange = resultVO.getResultScoreRange();
        ScoringResultEntity entity = this.getById(id);
        ThrowExceptionUtil.throwIf(entity == null, ErrorCode.ERROR_PARAM, "评分id错误");
        LambdaUpdateWrapper<ScoringResultEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScoringResultEntity::getId, id);
        updateWrapper.set(StrUtil.isNotBlank(resultName), ScoringResultEntity::getResultName, resultName);
        updateWrapper.set(StrUtil.isNotBlank(resultDesc), ScoringResultEntity::getResultDesc, resultDesc);
        updateWrapper.set(StrUtil.isNotBlank(resultPicture), ScoringResultEntity::getResultPicture, resultPicture);
        updateWrapper.set(StrUtil.isNotBlank(resultProp), ScoringResultEntity::getResultProp, resultProp);
        updateWrapper.set(resultScoreRange != null, ScoringResultEntity::getResultScoreRange, resultScoreRange);
        return this.update(updateWrapper);
    }

    @Override
    public Map<Long, List<ScoringResultVO>> selectMapByAll() {
        List<ScoringResultEntity> records = this.lambdaQuery()
                .select(ScoringResultEntity::getId,
                        ScoringResultEntity::getAppId,
                        ScoringResultEntity::getResultName,
                        ScoringResultEntity::getResultDesc,
                        ScoringResultEntity::getResultPicture,
                        ScoringResultEntity::getResultProp,
                        ScoringResultEntity::getResultScoreRange
                ).list();
        if (CollectionUtil.isEmpty(records)) return null;
        return records.stream().collect(Collectors.groupingBy(ScoringResultEntity::getAppId, Collectors.mapping(this::converterVO, Collectors.toList())));
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        return this.lambdaUpdate().eq(ScoringResultEntity::getAppId, appId).remove();
    }

    @Override
    public List<ScoringResultVO> selectListByAPPId(Long appId) {
        LambdaQueryWrapper<ScoringResultEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScoringResultEntity::getAppId, appId);
        return converterVO(this.list(queryWrapper));
    }

    @Override
    public ScoringResultVO selectByAppIdAndTypeCode(Long appId, String typeCode) {
        LambdaQueryWrapper<ScoringResultEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScoringResultEntity::getAppId, appId);
        queryWrapper.eq(ScoringResultEntity::getResultProp, typeCode);
        return converterVO(this.getOne(queryWrapper));
    }

    private List<ScoringResultVO> converterVO(List<ScoringResultEntity> records) {
        if (CollectionUtil.isEmpty(records)) return CollectionUtil.newArrayList();
        return records.stream().map(this::converterVO).collect(Collectors.toList());
    }

    private ScoringResultVO converterVO(ScoringResultEntity entity) {
        if (entity == null) return null;
        ScoringResultVO vo = new ScoringResultVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
