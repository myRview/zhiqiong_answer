package com.zhiqiong.controller;


import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.question.UserAnswerVO;
import com.zhiqiong.service.UserAnswerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户答题记录 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/user/answer")
//@Api(tags = "用户答题记录")
public class UserAnswerController {

    @Resource
    private UserAnswerService userAnswerService;

    @GetMapping("/list")
    @ApiOperation(value = "获取用户答案")
    public ResponseResult<List<UserAnswerVO>> selectAnswerListByApp(@RequestParam(value = "appId") Long appId, @RequestParam(value = "userId") Long userId) {
        List<UserAnswerVO> answerList = userAnswerService.selectAnswerListByApp(appId, userId);
        return ResponseResult.success(answerList);
    }


    @PostMapping("/submit")
    @ApiOperation(value = "提交答案")
    public ResponseResult<?> submitAnswer(@RequestBody UserAnswerVO userAnswerVO) {
        userAnswerService.submitAnswer(userAnswerVO);
        return ResponseResult.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除答案")
    public ResponseResult<?> deleteAnswer(@RequestBody IdVO idVO) {
        userAnswerService.removeById(idVO.getId());
        return ResponseResult.success();
    }

}
