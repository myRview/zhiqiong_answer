package com.zhiqiong.manager.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.config.CosConfig;
import com.zhiqiong.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author huangkun
 * @date 2024/12/16 18:33
 */
@Component
@Slf4j
public class CosManager {

    @Resource
    private COSClient cosClient;
    @Resource
    private CosConfig cosConfig;

    public PutObjectResult putObject(String key, File localFile) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, localFile);
        return cosClient.putObject(putObjectRequest);
    }

    public PutObjectResult putBaseImage(String key, File localFile) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, localFile);
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        PutObjectResult putObjectResult = null;
        try {
            putObjectResult = cosClient.putObject(putObjectRequest);
        } catch (CosClientException e) {
            log.error("上传失败", e);
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "上传失败");
        }
        return putObjectResult;
    }

    public String putImage(String key, String filePath, File localFile) {
        putBaseImage(key, localFile);
        return cosConfig.getHost() + filePath;
    }


    public void deleteObject(String key) {
        cosClient.deleteObject(cosConfig.getBucket(), key);
    }


}
