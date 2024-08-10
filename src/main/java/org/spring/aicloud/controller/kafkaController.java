package org.spring.aicloud.controller;

import jakarta.annotation.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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

    @RequestMapping("/send")
    public String send(String msg) {
        if (!StringUtils.hasLength(msg)) {
            return "请先输入发送的消息：";
        }
        kafkaTemplate.send(TOPIC, msg);

        return "ok";
    }

    @KafkaListener(topics = TOPIC)
    public void listen(String date) {
        System.out.println("收到消息：" + date);
    }
}
