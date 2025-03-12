package com.itdfq.common.aop.moreIn;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author dfq 2025/3/12 15:15
 * @implNote
 */
@Service
public class ThreadPoolConfig {
    @Bean
    public Map<ThreadPoolEnum, ThreadPoolExecutor> threadPools() {
        Map<ThreadPoolEnum, ThreadPoolExecutor> threadPools = new HashMap<>();

        threadPools.put(ThreadPoolEnum.THREAD_POOL_1, new ThreadPoolExecutor(
                5, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        ));

        threadPools.put(ThreadPoolEnum.THREAD_POOL_2, new ThreadPoolExecutor(
                10, 20, 120, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        ));

        // 可以添加更多的线程池配置

        return threadPools;
    }
}
