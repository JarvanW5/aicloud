package org.spring.aicloud.util;

/**
 * @Author: JarvanW
 * @Date: 2024/8/11
 * @Description:
 * @Requirements:
 */

/**
 * 全局变量
 */
public class AppVariable {

    // 讨论表点赞 Topic 名称
    public static final String DISCUSS_SUPPORT_TOPIC = "DISCUSS_SUPPORT_TOPIC";

    public static final int PAGE_SIZE = 3;            // todo: 后期上线，改成 15

    // 大模型调用分布式锁 key
    public static String getModelLockKey(Long uid, int model, int type) {
        return "MODEL_LOCK_KEY_" + uid + "_" + model + "_" + type;
    }
}
