package com.zhiqiong.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author huangkun
 * @date 2025/1/2 11:39
 */
@Configuration
@ConfigurationProperties(prefix = "thread.pool")
@Data
public class ThreadPoolProperties {

    private int corePoolSize = 8;
    private int maxPoolSize = 16;
    private long keepAliveTime = 30;
    private String unit = "SECONDS";
    private int queueCapacity = 100;
}
