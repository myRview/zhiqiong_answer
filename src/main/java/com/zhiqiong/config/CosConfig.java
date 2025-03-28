package com.zhiqiong.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huangkun
 * @date 2024/12/15 20:13
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosConfig {
    /**
     * 域名
     */
    private String host;
    /**
     * secretId
     */
    private String secretId;
    /**
     * secretKey
     */

    private String secretKey;
    /**
     * 地域
     */

    private String region;
    /**
     * 桶名
     */
    private String bucket;

    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);

    }


}
