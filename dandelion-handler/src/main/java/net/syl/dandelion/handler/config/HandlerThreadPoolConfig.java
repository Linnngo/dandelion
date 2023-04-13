package net.syl.dandelion.handler.config;

import java.util.concurrent.*;

/**
 * handler模块 线程池的配置
 * 暂时使用jdk原生线程池
 */
public class HandlerThreadPoolConfig {

    private static final String PRE_FIX = "dandelion.";
    private static final Integer COMMON_CORE_POOL_SIZE = 2;
    private static final Integer COMMON_MAX_POOL_SIZE = 2;
    private static final Integer COMMON_KEEP_LIVE_TIME = 60;
    private static final Integer COMMON_QUEUE_SIZE = 128;

    public static ExecutorService getExecutor() {

        return new ThreadPoolExecutor(
                COMMON_CORE_POOL_SIZE,
                COMMON_MAX_POOL_SIZE,
                COMMON_KEEP_LIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(COMMON_QUEUE_SIZE, false),
                new ThreadPoolExecutor.AbortPolicy());
    }


}
