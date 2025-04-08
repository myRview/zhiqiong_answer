package com.zhiqiong.manager.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.transfer.*;
import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.config.CosConfig;
import com.zhiqiong.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 20231
 */
@Component
@Slf4j
public class CosManager {
    private static final int MULTIPART_UPLOAD_THRESHOLD = 5 * 1024 * 1024;
    private static final int MINIMUM_UPLOAD_PART_SIZE = 1024 * 1024;
    private static final int PROGRESS_MONITOR_THREAD_POOL_SIZE = 20;

    @Resource
    private COSClient cosClient;
    @Resource
    private CosConfig cosConfig;


    private TransferManager transferManager;
    private ExecutorService progressMonitorExecutor;

    @PostConstruct
    public void init() {
        // 初始化TransferManager和共享线程池
        progressMonitorExecutor = Executors.newFixedThreadPool(PROGRESS_MONITOR_THREAD_POOL_SIZE);

        TransferManagerConfiguration config = new TransferManagerConfiguration();
        config.setMultipartUploadThreshold(MULTIPART_UPLOAD_THRESHOLD);
        config.setMinimumUploadPartSize(MINIMUM_UPLOAD_PART_SIZE);

        transferManager = new TransferManager(cosClient, progressMonitorExecutor);
        transferManager.setConfiguration(config);
    }

    @PreDestroy
    public void shutdown() {
        // 应用关闭时释放资源
        if (transferManager != null) {
            transferManager.shutdownNow(true);
        }
        if (progressMonitorExecutor != null) {
            progressMonitorExecutor.shutdownNow();
        }
    }

    // 创建通用PutObjectRequest
    private PutObjectRequest createPutRequest(String filePath, File localFile) {
        validateParams(filePath, localFile);
        return new PutObjectRequest(cosConfig.getBucket(), filePath, localFile);
    }

    // 参数校验
    private void validateParams(String filePath, File localFile) {
        if (StringUtils.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.ERROR_PARAM, "文件路径不能为空");
        }
    }

    public PutObjectResult putObject(String filePath, File localFile) {
        return cosClient.putObject(createPutRequest(filePath, localFile));
    }

    public PutObjectResult putBaseImage(String filePath, File localFile) {
        PutObjectRequest request = createPutRequest(filePath, localFile);
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        request.setPicOperations(picOperations);

        try {
            return cosClient.putObject(request);
        } catch (CosClientException e) {
            e.printStackTrace();
            log.info("图片上传失败 | filePath: {}", filePath, e);
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "图片上传失败");
        }
    }

    public String putImage(String filePath, File localFile) {
        putBaseImage(filePath, localFile);
        return buildFullUrl(filePath);
    }

    public void deleteObject(String filePath) {
        validateFilePath(filePath);
        cosClient.deleteObject(cosConfig.getBucket(), filePath);
    }

    private void validateFilePath(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.ERROR_PARAM, "文件路径不能为空");
        }
    }

    // 构建完整URL（优化：使用StringBuilder）
    private String buildFullUrl(String filePath) {
        StringBuilder urlBuilder = new StringBuilder(cosConfig.getHost());
        if (cosConfig.getHost().charAt(cosConfig.getHost().length() - 1) != '/') {
            urlBuilder.append('/');
        }
        urlBuilder.append(filePath.startsWith("/") ? filePath.substring(1) : filePath);
        return urlBuilder.toString();
    }

    // 优化进度监控（使用共享线程池）
    private void logTransferProgress(Transfer transfer) {
        progressMonitorExecutor.submit(() -> {
            do {
                TransferProgress progress = transfer.getProgress();
                if (log.isDebugEnabled()) {
                    log.debug("传输进度: {}/{} ({}%)",
                            progress.getBytesTransferred(),
                            progress.getTotalBytesToTransfer(),
                            String.format("%.2f", progress.getPercentTransferred()));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } while (!transfer.isDone());
            log.info("传输完成 | 状态: {}", transfer.getState());
        });
    }

    public UploadResult multipartUploadWithMetaData(String filePath, File localFile) {
        validateParams(filePath, localFile);
        try {
            Upload upload = transferManager.upload(createPutRequest(filePath, localFile));
            logTransferProgress(upload);
            return upload.waitForUploadResult();
        } catch (CosClientException | InterruptedException e) {
            log.info("分片上传失败 | filePath: {}", filePath, e);
            e.printStackTrace();
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "文件分片上传失败");
        }
    }

    public String multipartUpload(String filePath, File tempFile) {
        UploadResult uploadResult = multipartUploadWithMetaData(filePath, tempFile);
        return buildFullUrl(filePath);
    }
}