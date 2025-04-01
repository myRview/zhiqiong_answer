package com.zhiqiong.utils;

import com.zhiqiong.common.ErrorCode;
import com.zhiqiong.common.ThreadPoolProperties;
import com.zhiqiong.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * @author huangkun
 * @date 2024/10/24 21:01
 */
@Slf4j
@Component
public class ThreadPoolUtil {
    private static ThreadPoolExecutor poolExecutor;
    private static ThreadPoolProperties threadPoolProperties;

    @Autowired
    public ThreadPoolUtil(ThreadPoolProperties threadPoolProperties) {
        ThreadPoolUtil.threadPoolProperties = threadPoolProperties;
    }



    public static ExecutorService getExecutorService() {
        if (poolExecutor == null) {
            throw new BusinessException(ErrorCode.ERROR_SYSTEM, "线程池未初始化");
        }
        return poolExecutor;
    }
    @PostConstruct
    public void init() {
        int corePoolSize = threadPoolProperties.getCorePoolSize();
        int maximumPoolSize = threadPoolProperties.getMaxPoolSize();
        long keepAliveTime = threadPoolProperties.getKeepAliveTime();
        String unit = threadPoolProperties.getUnit();
        int queueCapacity = threadPoolProperties.getQueueCapacity();

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        poolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.valueOf(unit),
                workQueue,
                threadFactory,
                handler
        );
    }


    public static <T> Future<T> submit(Callable<T> task) {
        return poolExecutor.submit(task);
    }

    public static void shownDown() {
        if (poolExecutor != null) {
            poolExecutor.shutdown();
        }
    }

    public static void main(String[] args) {
    }

}
