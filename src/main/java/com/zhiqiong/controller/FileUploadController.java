package com.zhiqiong.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.manager.FileUploadService;
import com.zhiqiong.model.vo.IdVO;
import com.zhiqiong.model.vo.user.LoginUserVO;
import com.zhiqiong.model.vo.user.RegisterUserVO;
import com.zhiqiong.model.vo.user.UserPageVO;
import com.zhiqiong.model.vo.user.UserVO;
import com.zhiqiong.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author hk
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/file")
//@Api(tags = "文件管理")
public class FileUploadController {


    @Resource
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public ResponseResult<?> upload(@RequestParam("file") MultipartFile file,@RequestParam("type") String type){
        String fileUrl = fileUploadService.upload(file,type);
        return ResponseResult.success(fileUrl, "上传成功");
    }


}
