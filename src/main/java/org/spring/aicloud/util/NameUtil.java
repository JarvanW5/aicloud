package org.spring.aicloud.util;

import cn.hutool.crypto.SecureUtil;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @Author: JarvanW
 * @Date: 2024/8/1
 * @Description:
 * @Requirements:
 */

public class NameUtil {

    /**
     * 获取验证码
     * @param request
     * @return
     */
    public static String getCaptchaName(HttpServletRequest request){
        return "captcha-" + SecureUtil.md5(request.getRemoteAddr());
    }
}
