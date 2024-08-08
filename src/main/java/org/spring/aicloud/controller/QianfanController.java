package org.spring.aicloud.controller;


import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.baidubce.qianfan.model.image.Text2ImageResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.MinIoUtil;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 百度大模型（千帆）操作
 */
@RequestMapping("/qianfan")
@RestController
public class QianfanController {

    @Value("${qianfan.app-key}")
    private String apiKey;
    @Value("${qianfan.secret-key}")
    private String secretKey;

    @Resource
    private IAnswerService answerService;

    @Resource
    private MinIoUtil minIoUtil;


    /**
     * 聊天
     */

    @RequestMapping("/chat")
    public ResponseEntity chat(String question) {

        if (!StringUtils.hasLength(question)) {
            // 输入为空
            return ResponseEntity.error("请先输入内容!");
        }

        ChatResponse response = new Qianfan(apiKey, secretKey).chatCompletion()
                .model("ERNIE-Speed-8K")                       // 使用model指定预置模型
                .addMessage("user", question)         // 添加用户信息 （此方法可以调用多次，以实现多轮对话的消息传递）
                .execute();                                     // 发起请求

        String content = response.getResult();

        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(content);
        answer.setType(AiTypeEnum.CHAT.getValue());
        answer.setModel(AiModelEnum.QIANFAN.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        boolean save = answerService.save(answer);

        if (save) {
            return ResponseEntity.success(content);
        }
        return ResponseEntity.error("操作失败，请重试！");
    }

    /**
     * 获取聊天历史列表
     */

    @RequestMapping("/getchatlist")
    public ResponseEntity getChatList() {

        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model", AiModelEnum.QIANFAN.getValue());
        queryWrapper.eq("type", AiTypeEnum.CHAT.getValue());
        queryWrapper.eq("uid", SecurityUtil.getCurrentUser().getUid());
        queryWrapper.orderByDesc("aid");
        List<Answer> list = answerService.list(queryWrapper);
        return ResponseEntity.success(list);
    }


    /**
     * 绘画
     */
    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)) {
            // 输入为空
            return ResponseEntity.error("请先输入内容!");
        }

        Text2ImageResponse response = new Qianfan(apiKey, secretKey)
                .text2Image()
                .prompt(question)
                .execute();

        byte[] image = response.getData().get(0).getImage();
        String url = "";
        String fileName = "qf-" + UUID.randomUUID().toString().replace("-", "");
        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            url = minIoUtil.upload(fileName, inputStream, "image/png");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(url);
        answer.setModel(AiModelEnum.QIANFAN.getValue());
        answer.setType(AiTypeEnum.DRAW.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());

        boolean save = answerService.save(answer);
        if (save) {
            return ResponseEntity.success(url);
        }
        return ResponseEntity.error("操作失败，请重试！");
    }


    /**
     * 获取绘画历史列表
     */
    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {

        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model", AiModelEnum.QIANFAN.getValue());
        queryWrapper.eq("type", AiTypeEnum.DRAW.getValue());
        queryWrapper.eq("uid", SecurityUtil.getCurrentUser().getUid());
        queryWrapper.orderByDesc("aid");
        List<Answer> list = answerService.list(queryWrapper);
        return ResponseEntity.success(list);
    }


}
