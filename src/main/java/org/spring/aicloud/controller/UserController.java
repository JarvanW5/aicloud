package org.spring.aicloud.controller;

import org.spring.aicloud.util.ResponseEntity;
import org.springframework.util.StringUtils;
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

    // 登录 login
    @RequestMapping("/login")
    public ResponseEntity login(String username, String password) {
        // 1. 非空判断
        if (!StringUtils.hasLength(username)|| !StringUtils.hasLength(password)){
            return ResponseEntity.error("用户名或密码不能为空");
        }

        if ("admin".equals(username) && "admin".equals(password)){
            return ResponseEntity.success("登录成功");
        }
        return ResponseEntity.error("用户名或密码不正确");
    }

}
