package org.spring.aicloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.AppVariable;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: JarvanW
 * @Date: 2024/7/30
 * @Description:
 * @Requirements:
 */


@RestController
@RequestMapping("/openai")
public class OpenAIController {

    @Resource
    private OpenAiChatClient chatModel;
    @Resource
    private OpenAiImageClient imageModel;
    @Resource
    private IAnswerService answerService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;


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
            result = chatModel.call(question);
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

    /**
     * 调用 Open AI 绘画接口
     *
     * @param question
     * @return
     */
    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)) {
            // 输入为空
            return ResponseEntity.error("请先输入内容!");
        }

        // 执行分布式锁
        Long uid = SecurityUtil.getCurrentUser().getUid();
        String lockKey = AppVariable.getModelLockKey(uid,
                AiModelEnum.OPENAI.getValue(),
                AiTypeEnum.DRAW.getValue());
        String imgUrl = "";    // 大模型返回的结果
        boolean isSave = false;  // 数据添加的状态
        // 1、获取分布式锁实例
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            // 调用 Open AI 接口
            ImageResponse result = imageModel.call(new ImagePrompt(question));
            imgUrl = result.getResult().getOutput().getUrl();


            // 将结果保存到数据库
            Answer answer = new Answer();
            answer.setTitle(question);
            answer.setContent(imgUrl);
            answer.setModel(AiModelEnum.OPENAI.getValue());
            answer.setType(AiTypeEnum.DRAW.getValue());
            answer.setUid(SecurityUtil.getCurrentUser().getUid());


            isSave = answerService.save(answer);
        } catch (Exception e) {

        } finally {
            rLock.unlock();
        }
        if (isSave) {
            return ResponseEntity.success(imgUrl);
        }

        return ResponseEntity.error("数据保存失败，请重试！");
    }

    /**
     * 获取聊天历史记录
     *
     * @return
     */
    @RequestMapping("/getchatlist")
    // todo: 缓存,Openai的已经完成了
    public ResponseEntity getChatList() {
        Long uid = SecurityUtil.getCurrentUser().getUid();
        int model = AiModelEnum.OPENAI.getValue();
        int type = AiTypeEnum.CHAT.getValue();
        String cacheKey = AppVariable.getListCacheKey(uid, model, type);
        Object list = redisTemplate.opsForValue().get(cacheKey);
        if (list == null) {   // 缓存不存在
            QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", uid);
            queryWrapper.eq("type", type);
            queryWrapper.eq("model", model);
            queryWrapper.orderByDesc("aid");
            List<Answer> dataList = answerService.list(queryWrapper);

            // 存储到缓存中
            redisTemplate.opsForValue().set(cacheKey, dataList, 1, TimeUnit.DAYS);
            return ResponseEntity.success(dataList);
        }else {
            System.out.println("缓存命中，触发了 Redis 缓存~~~~~~~~~~~~~~");
            return ResponseEntity.success(list);
        }
    }

    /**
     * 获取绘画历史记录
     *
     * @return
     */

    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {
        Long uid = SecurityUtil.getCurrentUser().getUid();
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", AiTypeEnum.DRAW.getValue());
        queryWrapper.eq("model", AiModelEnum.OPENAI.getValue());
        queryWrapper.orderByDesc("aid");

        return ResponseEntity.success(answerService.list(queryWrapper));
    }
}
