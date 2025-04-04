package com.zhiqiong.controller;


import com.zhiqiong.common.ResponseResult;
import com.zhiqiong.manager.file.FileUploadService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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
