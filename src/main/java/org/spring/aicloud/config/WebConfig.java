package org.spring.aicloud.config;

import jakarta.annotation.Resource;
import org.spring.aicloud.util.idempotent.IdempotentInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: JarvanW
 * @Date: 2024/8/13
 * @Description:
 * @Requirements:
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private IdempotentInterceptor idempotentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(idempotentInterceptor)
                .addPathPatterns("/**");   // 拦截所有的请求
    }
}
