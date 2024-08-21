package org.spring.aicloud.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.util.AppVariable;
import org.spring.aicloud.util.idempotent.Idempotent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: JarvanW
 * @Date: 2024/8/10
 * @Description:
 * @Requirements:
 */
@RefreshScope
@RestController
@RequestMapping("/kafka")
public class kafkaController {
    private static final String TOPIC = "aicloud";
    // canal 将 mysql binlog 同步到 kafka 中的 topic
    private static final String CANAL_TOPIC = "ai-cloud-canal-to-kafka";

    @Resource
    private KafkaTemplate kafkaTemplate;


    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${mytest:javacn.set}")
    private String mytest;

    @RequestMapping("/test")
    public String test() {
        return "hello world";
    }

    @RequestMapping("/getconfig")
    public String getConfig() {
        return mytest;
    }

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

    @KafkaListener(topics = {CANAL_TOPIC})
    public void canalListen(String date, Acknowledgment acknowledgment) throws JsonProcessingException {
        HashMap<String, Object> map = objectMapper.readValue(date, HashMap.class);
        if (!map.isEmpty() && map.get("database").toString().equals("aicloud") && map.get("table").toString().equals("answer")) {
            // 更新 Redis 缓存
            ArrayList<LinkedHashMap<String, Object>> list =
                    (ArrayList<LinkedHashMap<String, Object>>) map.get("data");
            String cacheKey = "";
            for (LinkedHashMap<String, Object> answer : list){
                cacheKey = AppVariable.getListCacheKey(
                        Long.parseLong(answer.get("uid").toString()),
                        Integer.parseInt(answer.get("model").toString()),
                        Integer.parseInt(answer.get("type").toString()));
                redisTemplate.opsForValue().set(cacheKey, null);
            }

        }

        // 手动确认应答
        acknowledgment.acknowledge();
    }
}
