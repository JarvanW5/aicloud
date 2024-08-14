package org.spring.aicloud.controller;

import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.spring.aicloud.util.idempotent.Idempotent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @RequestMapping("/idtest")
    @Idempotent
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
