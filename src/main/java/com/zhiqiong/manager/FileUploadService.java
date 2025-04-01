package com.zhiqiong.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.UploadResult;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.exception.BusinessException;
import com.zhiqiong.manager.cos.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author huangkun
 * @date 2025/3/28 15:01
 */
@Component
@Slf4j
public class FileUploadService {

    @Resource
    private CosManager cosManager;


    public String upload(MultipartFile file, String type) {

        long start = System.currentTimeMillis();
        long size = file.getSize();
//        if (size > 1024 * 1024 ) {
//            //TODO：采用分片上传
//
//
//        }
        String imageUrl = null;
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        //上传文件
        String uuid = RandomUtil.randomString(16);
        String fileName = String.format("%s_%s.%s", DateUtil.format(new Date(), "yyyyMMddHHmmss"), uuid, fileSuffix);
        String filePath = String.format("/%s/%s", type, fileName);
        //创建临时文件
        try {
            File tempFile = File.createTempFile(filePath, null);
            file.transferTo(tempFile);
            //上传文件
//            imageUrl = cosManager.putImage(filePath,filePath, tempFile);
            imageUrl = cosManager.multipartUpload(filePath, tempFile);
            log.error("上传成功，返回结果：{}", imageUrl);
        } catch (IOException e) {
            log.error("上传失败", e);
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "上传失败");
        }
        log.error("上传文件耗时：{}ms", System.currentTimeMillis() - start);
        return imageUrl;
    }
}
