package org.spring.aicloud.config;

/**
 * @Author: JarvanW
 * @Date: 2024/8/8
 * @Description:
 * @Requirements:
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 创建线程池
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        // 最大线程数
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        // 任务队列容量
        executor.setQueueCapacity(100000);
        // 执行自定义拒绝策略
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                // todo:1\ 保存拒绝任务


                // todo:2\ 通知相关负责人进行手动处理

            }
        });

        executor.initialize();

        return executor;
    }
}
