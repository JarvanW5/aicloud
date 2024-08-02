package org.spring.aicloud.config;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.spring.aicloud.entity.SecurityUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author: JarvanW
 * @Date: 2024/8/2
 * @Description:
 * @Requirements:
 */

/**
 * 登录认证过滤器
 */
@Component
public class LoginAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1、 获取 JWT 令牌
        String token = request.getHeader("Authorization");

        // 2、 判断 JWT 令牌是否为空
        if (StringUtils.hasLength(token)) {
            // 3、 如果不为空，判断 JWT 令牌的正确性
            if (JWTUtil.verify(token, jwtSecret.getBytes())) {
                // 4、 获取用户信息存储到 Security 中
                JWT jwt = JWTUtil.parseToken(token);
                if (jwt != null && jwt.getPayload("uid") != null) {
                    Long uid = Long.parseLong(jwt.getPayload("uid").toString());
                    String username = (String) jwt.getPayload("username");
                    // 创建用户对象
                    SecurityUserDetails userDetails = new SecurityUserDetails(uid,username,"");
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
