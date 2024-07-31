package org.spring.aicloud.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.net.NetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.spring.aicloud.util.MinIoUtil;
import org.spring.aicloud.util.ResponseEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: JarvanW
 * @Date: 2024/7/31
 * @Description:
 * @Requirements:
 */

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private MinIoUtil minIoUtil;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 生成验证码
     */
    @RequestMapping("/create")
    public ResponseEntity createCaptcha(HttpServletRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

//        // 操作 String 类型
//        redisTemplate.opsForValue().set("key", "value");      // 存
//        redisTemplate.opsForValue().get("key");     // 取
//
//
//        // 操作 Hash
//        redisTemplate.opsForHash().put("hashKey", "hashField", "hashValue");
//        redisTemplate.opsForHash().get("hashKey", "hashField");
//
//
//        // 操作 List
//        redisTemplate.opsForList().leftPush("listKey", "listValue");
//        redisTemplate.opsForList().rightPop("listKey");
//
//
//        // 操作 Set
//        redisTemplate.opsForSet().add("setKey", "setValue");
//        redisTemplate.opsForSet().members("setKey");
//
//        // 操作 Zset
//        redisTemplate.opsForZSet().add("zsetKey", "zsetValue", 1.0);
//        redisTemplate.opsForZSet().range("zsetKey", 0, -1);

        String url = "";
        // 定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40);
        String fileName = "captcha-" + SecureUtil.md5(request.getRemoteAddr());
        try (InputStream inputStream = new ByteArrayInputStream(lineCaptcha.getImageBytes())) {
            url = minIoUtil.upload(fileName, inputStream, "image/png");
            String code = lineCaptcha.getCode();      // 正确的验证码
            redisTemplate.opsForValue().set(fileName, code,60, TimeUnit.SECONDS);   // 验证码存储到Redis中
        }
        return ResponseEntity.success(url);
    }
}
