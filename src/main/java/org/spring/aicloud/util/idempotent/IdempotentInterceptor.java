package org.spring.aicloud.util.idempotent;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.spring.aicloud.entity.SecurityUserDetails;

import org.spring.aicloud.util.SecurityUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import java.util.concurrent.TimeUnit;

@Component
public class IdempotentInterceptor implements HandlerInterceptor {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Idempotent idempotent = null;
        try {
            idempotent = ((HandlerMethod) handler)
                    .getMethod().getAnnotation(Idempotent.class);
        } catch (Exception e) {
        }
        if (idempotent != null) { // 当前方法添加幂等性注解
            createId(request);
            String id = createId(request);
            if (redisTemplate.opsForValue().get(id) != null) {
                response.getWriter().write("do idepotent~");
                return false;
            } else {
                redisTemplate.opsForValue().set(id, "true", idempotent.time(),
                        TimeUnit.SECONDS);
                return true;
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 生成幂等性 id：md5(用户 ID + 请求参数)
     *
     * @param request
     */
    private String createId(HttpServletRequest request) throws JsonProcessingException {
        Long uid = 0L;
        SecurityUserDetails userDetails = SecurityUtil.getCurrentUser();
        if (userDetails != null) {
            uid = userDetails.getUid();
        }
        String requestParam = objectMapper.writeValueAsString(request.getParameterMap());
        return SecureUtil.md5(uid + requestParam);
    }
}
