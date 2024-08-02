package org.spring.aicloud.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.spring.aicloud.entity.User;
import org.spring.aicloud.entity.dto.UserDTO;
import org.spring.aicloud.service.IUserService;
import org.spring.aicloud.util.NameUtil;
import org.spring.aicloud.util.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

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
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IUserService userService;

    @Value("${jwt.secret}")
    private String jwtsecret;

    /**
     * 登录方法
     *
     * @param userDTO
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public ResponseEntity login(@Validated UserDTO userDTO, HttpServletRequest request) {
        // 1. 验证图片验证码
        String redisCaptchaKey = NameUtil.getCaptchaName(request);
        String redisCaptcha = (String) redisTemplate.opsForValue().get(redisCaptchaKey);
        if (!StringUtils.hasLength(redisCaptcha) || !redisCaptcha.equalsIgnoreCase(userDTO.getCaptcha())) {
            return ResponseEntity.error("输入验证码错误，请重新输入！");
        }

        // 2. 验证用户名密码
        QueryWrapper<User> querywrapper = new QueryWrapper<>();
        querywrapper.eq("username", userDTO.getUsername());
        User user = userService.getOne(querywrapper);
        if (user != null && passwordEncoder.matches(userDTO.getPassword(),
                user.getPassword())) {
            // 生成JWT
            HashMap<String, Object> payLoad = new HashMap<>();
            payLoad.put("uid",user.getUid());
            payLoad.put("username",user.getUsername());
            HashMap<String, String> result = new HashMap<>();
            result.put("jwt",JWTUtil.createToken(payLoad,jwtsecret.getBytes()));
            result.put("username", user.getUsername());
            return ResponseEntity.success(result);
        }
        return ResponseEntity.error("用户名或密码不正确");
    }


    /**
     * 注册功能
     */

    @RequestMapping("/register")
    public ResponseEntity rsgister(@Validated User user) {
        // 将密码进行加盐处理
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 添加方法

        boolean result = userService.save(user);
        if (result) {
            return ResponseEntity.success("注册成功");
        }
        return ResponseEntity.error("注册失败");
    }

    @RequestMapping("/test")
    public String test() {

        return "OK";
    }

}
