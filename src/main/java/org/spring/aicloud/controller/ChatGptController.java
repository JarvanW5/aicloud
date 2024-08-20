package org.spring.aicloud.controller;

/**
 * @Author: JarvanW
 * @Date: 2024/8/20
 * @Description:
 * @Requirements:
 */

import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.AppVariable;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 调用本地大模型
 */
@RestController
@RequestMapping("/chatgpt")
public class ChatGptController {

    @Resource
    private IAnswerService answerService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private OllamaChatClient ollamaChatClient;




    /**
     * 调用 Open AI 聊天接口
     *
     * @param question
     * @return
     */
    @RequestMapping("/chat")
    // todo: 添加分布式锁，openai的已经完成
    public ResponseEntity chat(String question) throws InterruptedException {
        if (!StringUtils.hasLength(question)) {
            // 输入为空
            return ResponseEntity.error("请先输入内容!");
        }
        // 执行分布式锁
        Long uid = SecurityUtil.getCurrentUser().getUid();
        String lockKey = AppVariable.getModelLockKey(uid,
                AiModelEnum.OPENAI.getValue(),
                AiTypeEnum.CHAT.getValue());
        String result = "";    // 大模型返回的结果
        boolean isSave = false;  // 数据添加的状态
        // 1、获取分布式锁实例
        RLock rLock = redissonClient.getLock(lockKey);
        try {

            // 2、获取分布式锁
            boolean isLock = rLock.tryLock(30, TimeUnit.SECONDS);
            if (!isLock) {
                // 获取分布式锁失败
                return ResponseEntity.error("请勿频繁请求，请稍后再试！");
            }

            // 3、释放分布式锁

            // 调用 Open AI 接口
            result = ollamaChatClient.call(question);
            // 将结果保存到数据库
            Answer answer = new Answer();
            answer.setTitle(question);
            answer.setContent(result);
            answer.setModel(AiModelEnum.OPENAI.getValue());
            answer.setType(AiTypeEnum.CHAT.getValue());
            answer.setUid(SecurityUtil.getCurrentUser().getUid());

            isSave = answerService.save(answer);
        } catch (Exception e) {

        } finally {
            // 3、释放分布式锁
            rLock.unlock();
        }
        if (isSave) {
            return ResponseEntity.success(result);
        }

        return ResponseEntity.error("数据保存失败，请重试！");
    }


}
