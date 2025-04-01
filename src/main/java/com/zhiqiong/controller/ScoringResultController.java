package com.zhiqiong.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.score.ScoreResultPageVO;
import com.zhiqiong.model.vo.score.ScoringResultVO;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.service.ScoringResultService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 评分结果 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/scoring/result")
//@Api(tags = "评分结果")
public class ScoringResultController {
    @Resource
    private ScoringResultService scoringResultService;

    @PostMapping("/add")
    @ApiOperation(value = "添加评分规则")
    public ResponseResult<Boolean> addScoreResult(@RequestBody ScoringResultVO resultVO) {
        boolean add = scoringResultService.addScoreResult(resultVO);
        return ResponseResult.success(add);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取评分结果列表")
    public ResponseResult<List<ScoringResultVO>> selectScoreResultList(@RequestParam(value = "resultName", required = false) String resultName) {
//        List<ScoringResultVO> resultList = scoringResultService.selectScoreResultList(resultName);
        return ResponseResult.success(Collections.emptyList());
    }

    @PostMapping("/page")
    @ApiOperation(value = "获取评分结果分页列表")
    public ResponseResult<Page<ScoringResultVO>> selectScoreResultPage(@RequestBody ScoreResultPageVO pageVO) {
        Page<ScoringResultVO> page = scoringResultService.selectScoreResultPage(pageVO);
        return ResponseResult.success(page);
    }


    @DeleteMapping("/delete")
    @ApiOperation(value = "删除评分结果")
    public ResponseResult<Boolean> deleteScoreResult(@RequestBody IdVO idVO) {
        boolean remove = scoringResultService.removeById(idVO.getId());
        return ResponseResult.success(remove);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新评分结果")
    public ResponseResult<Boolean>updateScoreResult(@RequestBody ScoringResultVO resultVO) {
        boolean update = scoringResultService.updateScoreResult(resultVO);
        return ResponseResult.success(update);
    }



}
