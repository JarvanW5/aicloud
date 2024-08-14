package org.spring.aicloud.task;

/**
 * @Author: JarvanW
 * @Date: 2024/8/14
 * @Description:
 * @Requirements:
 */

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.User;
import org.spring.aicloud.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 存放定时任务
 */
@Component
public class TimedTask {

    @Resource
    private IUserService userService;
    @Value("${system.user.use-count}")
    private Integer useCount;

    /**
     * 重置用户使用次数的定时任务
     */

    @XxlJob("resetUserUseCont")
    public void resetUserUseCont() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("usecount", useCount);
        if (!userService.update(wrapper)) {
            // todo: 调用通知中心通知相关负责人排查和解决问题
        }
        System.out.println("重置用户使用次数的定时任务");
    }
}
