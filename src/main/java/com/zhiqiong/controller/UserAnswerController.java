package com.zhiqiong.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.answwer.AnswerPageVO;
import com.zhiqiong.model.vo.answwer.UserAnswerVO;
import com.zhiqiong.service.UserAnswerService;
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

    @GetMapping("/id")
    @ApiOperation(value = "获取用户答案详情")
    public ResponseResult<UserAnswerVO> selectAnswerById(@RequestParam(value = "id") Long id) {
        UserAnswerVO answerList = userAnswerService.selectAnswerById(id);
        return ResponseResult.success(answerList);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取用户答案")
    public ResponseResult<List<UserAnswerVO>> selectAnswerListByApp(@RequestParam(value = "appId") Long appId, @RequestParam(value = "userId") Long userId) {
        List<UserAnswerVO> answerList = userAnswerService.selectAnswerListByApp(appId, userId);
        return ResponseResult.success(answerList);
    }

    @PostMapping("/page")
    @ApiOperation(value = "获取答题记录分页列表")
    public ResponseResult<Page<UserAnswerVO>> selectAnswerPage(@RequestBody AnswerPageVO answerPageVO) {
        Page<UserAnswerVO> page = userAnswerService.selectAnswerPage(answerPageVO);
        return ResponseResult.success(page);
    }



    @PostMapping("/submit")
    @ApiOperation(value = "提交答案")
    public ResponseResult<UserAnswerVO> submitAnswer(@RequestBody UserAnswerVO userAnswerVO) {
        UserAnswerVO answerVO = userAnswerService.submitAnswer(userAnswerVO);
        return ResponseResult.success(answerVO);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除答案")
    public ResponseResult<?> deleteAnswer(@RequestBody IdVO idVO) {
        userAnswerService.removeById(idVO.getId());
        return ResponseResult.success();
    }

}
