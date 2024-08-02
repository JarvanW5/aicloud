package org.spring.aicloud.config;

/**
 * @Author: JarvanW
 * @Date: 2024/8/1
 * @Description:
 * @Requirements:
 */

import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security 框架配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private LoginAuthenticationFilter loginAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)          // 禁用 明文 验证
                .csrf(AbstractHttpConfigurer::disable)          // 禁用 CSRF 验证
                .formLogin(AbstractHttpConfigurer::disable)          // 禁用默认登录页面
                .logout(AbstractHttpConfigurer::disable)         // 禁用默认注销页
                .headers(AbstractHttpConfigurer::disable)         // 禁用默认的 Header （支持 iframe 访问页面）
                .sessionManagement(session ->                      // 禁用 Session （项目中使用了 JWT 认证）
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(      // 允许访问的资源
                                        "/layui/**",
                                        "/layui/js/**",
                                        "/layui/css/**",
                                        "/login.html",
                                        "/index.html",
                                        "/register.html",
                                        "/user/login",
                                        "/user/register",
                                        "/captcha/create"
                                ).permitAll()
                                .anyRequest().authenticated()         // 其他请求都需要认证拦截
                )
                // 添加自定义登录认证过滤器
                .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
