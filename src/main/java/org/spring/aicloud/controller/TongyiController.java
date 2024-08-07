package org.spring.aicloud.controller;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatClient;
import com.alibaba.cloud.ai.tongyi.chat.TongYiChatOptions;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesClient;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;

/**
 * @Author: JarvanW
 * @Date: 2024/8/3
 * @Description:
 * @Requirements:
 */

@RestController
@RequestMapping("/tongyi")
public class TongyiController {

    @Resource
    private TongYiChatClient chatClient;

    @Resource
    private TongYiImagesClient imageClient;

    @Resource
    private IAnswerService answerService;

    /**
     * 聊天
     *
     * @param question
     * @return
     */
    @RequestMapping("/chat")
    public ResponseEntity chat(String question) {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.error("问题不能为空");
        }
        String result = chatClient.call(new Prompt(question))
                .getResult()
                .getOutput()
                .getContent();

        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(result);
        answer.setModel(AiModelEnum.TONGYI.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        answer.setType(AiTypeEnum.CHAT.getValue());

        boolean saveResult = answerService.save(answer);
        if (saveResult) {
            return ResponseEntity.success(result);
        }
        return ResponseEntity.error("保存失败,请重试！");
    }

    /**
     * 绘画
     *
     * @param question
     * @return
     */
    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.error("问题不能为空");
        }
        String result = imageClient.call(new ImagePrompt(question))
                .getResult().getOutput().getUrl();

        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(result);
        answer.setModel(AiModelEnum.TONGYI.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        answer.setType(AiTypeEnum.DRAW.getValue());
        boolean saveResult = answerService.save(answer);
        if (saveResult) {
            return ResponseEntity.success(result);
        }
        return ResponseEntity.error("保存失败,请重试！");
    }

    /**
     * 获取聊天历史列表
     */

    @RequestMapping("/getchatlist")
    public ResponseEntity getChatList() {

        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model", AiModelEnum.TONGYI.getValue());
        queryWrapper.eq("type", AiTypeEnum.CHAT.getValue());
        queryWrapper.eq("uid", SecurityUtil.getCurrentUser().getUid());
        queryWrapper.orderByDesc("aid");
        List<Answer> list = answerService.list(queryWrapper);
        return ResponseEntity.success(list);
    }


    /**
     * 获取绘画历史列表
     */
    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {

        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model", AiModelEnum.TONGYI.getValue());
        queryWrapper.eq("type", AiTypeEnum.DRAW.getValue());
        queryWrapper.eq("uid", SecurityUtil.getCurrentUser().getUid());
        queryWrapper.orderByDesc("aid");
        List<Answer> list = answerService.list(queryWrapper);
        return ResponseEntity.success(list);
    }
}
