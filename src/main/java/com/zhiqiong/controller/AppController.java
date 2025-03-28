package com.zhiqiong.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.app.AppPageVO;
import com.zhiqiong.model.vo.app.AppVO;
import com.zhiqiong.model.vo.app.OperateAppVO;
import com.zhiqiong.model.vo.app.ReviewAppVO;
import com.zhiqiong.service.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 应用 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/app")
//@Api(tags = "应用管理")
public class AppController {
    @Resource
    private AppService appService;

    @GetMapping("/id")
    @ApiOperation(value = "获取应用详情")
    public ResponseResult<AppVO> selectAppInfo(@RequestParam(value = "id") Long id) {
        AppVO appVO = appService.selectAppInfo(id);
        return ResponseResult.success(appVO);
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取应用列表")
    public ResponseResult<List<AppVO>> selectAppList(@RequestParam(value = "appName", required = false) String appName,
                                                     @RequestParam(value = "reviewStatus", required = false) Integer reviewStatus) {
        List<AppVO> appList = appService.selectAppList(appName, reviewStatus);
        return ResponseResult.success(appList);
    }

    @PostMapping("/page")
    @ApiOperation(value = "获取应用分页列表")
    public ResponseResult<Page<AppVO>> selectAppPage(@RequestBody AppPageVO pageVO) {
        Page<AppVO> appPage = appService.selectAppPage(pageVO);
        return ResponseResult.success(appPage);
    }

    @GetMapping("/user/list")
    @ApiOperation(value = "获取用户应用列表")
    public ResponseResult<List<AppVO>> selectAppListByUser(@RequestParam(value = "userId") Long userId) {
        List<AppVO> appList = appService.selectAppListByUser(userId);
        return ResponseResult.success(appList);
    }


    @PostMapping("/add")
    @ApiOperation(value = "添加应用")
    public ResponseResult addApp(@RequestBody OperateAppVO appVO) {
        appService.addApp(appVO);
        return ResponseResult.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除应用")
    public ResponseResult deleteApp(@RequestBody IdVO idVO) {
        appService.deleteApp(idVO);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新应用")
    public ResponseResult updateApp(@RequestBody OperateAppVO updateVO) {
        appService.updateApp(updateVO);
        return ResponseResult.success();
    }


    @PostMapping("/review")
    @ApiOperation(value = "审核应用")
    public ResponseResult<Boolean> reviewApp(@RequestBody ReviewAppVO reviewAppVO) {
        boolean update = appService.reviewApp(reviewAppVO);
        return ResponseResult.success(update);
    }

}
