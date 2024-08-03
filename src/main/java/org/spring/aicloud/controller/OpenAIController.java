package org.spring.aicloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * 调用 Open AI 聊天接口
     *
     * @param question
     * @return
     */
    @RequestMapping("/chat")
    public ResponseEntity chat(String question) {
        if (!StringUtils.hasLength(question)) {
            // 输入为空
            return ResponseEntity.error("请先输入内容!");
        }
        // 调用 Open AI 接口
        String result = chatModel.call(question);
        // 将结果保存到数据库
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(result);
        answer.setModel(1);
        answer.setType(1);
        answer.setUid(SecurityUtil.getCurrentUser().getUid());


        boolean addResult = answerService.save(answer);
        if (addResult) {
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
        // 调用 Open AI 接口
        ImageResponse result = imageModel.call(new ImagePrompt(question));
        String imgUrl = result.getResult().getOutput().getUrl();


        // 将结果保存到数据库
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(imgUrl);
        answer.setModel(1);
        answer.setType(2);
        answer.setUid(SecurityUtil.getCurrentUser().getUid());


        boolean addResult = answerService.save(answer);
        if (addResult) {
            return ResponseEntity.success(imgUrl);
        }

        return ResponseEntity.error("数据保存失败，请重试！");
    }

    /**
     * 获取聊天历史记录
     * @return
     */
    @RequestMapping("/getchatlist")
    public ResponseEntity getChatList() {
        Long uid = SecurityUtil.getCurrentUser().getUid();
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", 1);
        queryWrapper.eq("model", 1);
        queryWrapper.orderByDesc("aid");

        return ResponseEntity.success(answerService.list(queryWrapper));
    }

    /**
     * 获取绘画历史记录
     * @return
     */

    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {
        Long uid = SecurityUtil.getCurrentUser().getUid();
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", 2);
        queryWrapper.eq("model", 1);
        queryWrapper.orderByDesc("aid");

        return ResponseEntity.success(answerService.list(queryWrapper));
    }
}
