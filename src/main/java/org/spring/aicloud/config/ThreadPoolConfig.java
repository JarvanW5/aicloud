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
        executor.setThreadNamePrefix("thread-pool-");

        return null;
    }
}
