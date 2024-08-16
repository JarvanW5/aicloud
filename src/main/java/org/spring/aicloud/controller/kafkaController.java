package org.spring.aicloud.controller;

import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.spring.aicloud.util.AppVariable;
import org.spring.aicloud.util.idempotent.Idempotent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author: JarvanW
 * @Date: 2024/8/10
 * @Description:
 * @Requirements:
 */

@RestController
@RequestMapping("/kafka")
public class kafkaController {
    private static final String TOPIC = "aicloud";

    @Resource
    private KafkaTemplate kafkaTemplate;


    @Resource
    private RedissonClient redissonClient;

    @RequestMapping("/testlock")
    public String testlock() {
        String msg = "";
        String lockKey = AppVariable.getModelLockKey(1L, 1, 1);
        boolean isLock = false;
        try {
            isLock = redissonClient.getLock(lockKey).tryLock(30, TimeUnit.SECONDS);
            msg = "获得分布式加锁：" + isLock;
            System.out.println(msg);
        } catch (Exception e) {

        } finally {
            redissonClient.getLock(lockKey).unlock();
        }

        if (!isLock) {
            msg = "获得分布式加锁失败~";
            System.out.println(msg);
        }
        return msg;
    }


    @RequestMapping("/idtest")
//    @Idempotent
    public String idtest(String data) {
        return "data:" + data;
    }

    @XxlJob("testjob")
    public void testjob() {
        System.out.println("执行了定时任务");
    }

    @RequestMapping("/send")
    public String send(String msg) {
        if (!StringUtils.hasLength(msg)) {
            return "请先输入发送的消息：";
        }
        kafkaTemplate.send(TOPIC, msg);

        return "ok";
    }

    @KafkaListener(topics = TOPIC)
    public void listen(String date, Acknowledgment acknowledgment) {
        System.out.println("收到消息：" + date);

        // 手动确认应答
        acknowledgment.acknowledge();
    }
}
