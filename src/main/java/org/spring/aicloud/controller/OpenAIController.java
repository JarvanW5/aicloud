package org.spring.aicloud.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.spring.aicloud.util.ResponseEntity;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
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
    private OpenAiChatModel chatModel;
    @Resource
    private OpenAiImageModel imageModel;


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
        return ResponseEntity.success(result);
    }

    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)) {
            // 输入为空
            return ResponseEntity.error("请先输入内容!");
        }
        // 调用 Open AI 接口
        ImageResponse result = imageModel.call(new ImagePrompt(question));
        return ResponseEntity.success(result.getResults().get(0));
    }
}
