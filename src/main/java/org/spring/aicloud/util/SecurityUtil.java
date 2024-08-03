package org.spring.aicloud.util;

import org.spring.aicloud.entity.SecurityUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author: JarvanW
 * @Date: 2024/8/3
 * @Description:
 * @Requirements:
 */

public class SecurityUtil {
    /**
     * 得到当前登录用户
     *
     * @return
     */

    public static SecurityUserDetails getCurrentUser() {
        return (SecurityUserDetails)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
