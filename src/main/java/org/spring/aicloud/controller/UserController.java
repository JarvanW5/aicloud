package org.spring.aicloud.controller;

import cn.hutool.crypto.SecureUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.spring.aicloud.entity.User;
import org.spring.aicloud.service.IUserService;
import org.spring.aicloud.util.ResponseEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: JarvanW
 * @Date: 2024/7/31
 * @Description:
 * @Requirements:
 */

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IUserService userService;

    // 登录 login
    @RequestMapping("/login")
    public ResponseEntity login(String username, String password, String captcha, HttpServletRequest request) {
        // 1. 非空判断
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password) || !StringUtils.hasLength(captcha)) {
            return ResponseEntity.error("参数有误！");
        }

        // 2. 验证图片验证码
        String redisCaptchaKey = "captcha-" + SecureUtil.md5(request.getRemoteAddr());
        String redisCaptcha = (String) redisTemplate.opsForValue().get(redisCaptchaKey);
        if (!StringUtils.hasLength(redisCaptcha) || !redisCaptcha.equalsIgnoreCase(captcha)) {
            return ResponseEntity.error("输入验证码错误，请重新输入！");
        }

        // 3. 验证用户名密码
        if ("admin".equals(username) && "admin".equals(password)) {
            return ResponseEntity.success("登录成功");
        }
        return ResponseEntity.error("用户名或密码不正确");
    }


    /**
     * 添加用户
     */

    @RequestMapping("/add")
    public ResponseEntity add(User user) {

        user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        boolean result = userService.save(user);
        return ResponseEntity.success("result: " + result);
    }

}
