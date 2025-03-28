package com.zhiqiong.controller;


import com.zhiqiong.service.ScoringResultService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

}
