package com.zhiqiong.controller;


import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.model.vo.RegisterUserVO;
import com.zhiqiong.service.AppService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
public class AppController {
    @Resource
    private AppService appService;



}
