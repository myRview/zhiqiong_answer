package com.zhiqiong.controller;


import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.question.AddQuestionVO;
import com.zhiqiong.model.vo.question.QuestionVO;
import com.zhiqiong.model.vo.question.TopicVO;
import com.zhiqiong.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 题目 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/question")
@Api(tags = "题目管理")
public class QuestionController {
    
    @Resource
    private QuestionService questionService;


    @GetMapping("/id")
    @ApiOperation(value = "获取题目详情")
    public ResponseResult<?> selectQuestionInfo(@RequestParam(value = "id") Long id) {
        TopicVO topicVO = questionService.selectTopicInfo(id);
        return ResponseResult.success(topicVO);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取题目列表")
    public ResponseResult<AddQuestionVO> selectQuestionList(@RequestParam(value = "appId", required = false) Long appId) {
        AddQuestionVO questionVOList = questionService.selectQuestionList(appId);
        return ResponseResult.success(questionVOList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加题目")
    public ResponseResult<Boolean> addQuestion(@RequestBody AddQuestionVO addQuestionVO) {
        boolean add = questionService.addQuestion(addQuestionVO);
        return ResponseResult.success(add);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除题目")
    public ResponseResult<Boolean> deleteQuestion(@RequestBody IdVO idVO) {
        Long id = idVO.getId();
        boolean del = questionService.removeById(id);
        return ResponseResult.success(del);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新题目")
    public ResponseResult<Boolean> updateQuestion(@RequestBody TopicVO topicVO) {
        boolean update = questionService.updateTopic(topicVO);
        return ResponseResult.success(update);
    }

}
