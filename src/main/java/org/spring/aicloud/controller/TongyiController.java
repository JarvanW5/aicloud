package org.spring.aicloud.controller;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatClient;
import com.alibaba.cloud.ai.tongyi.chat.TongYiChatOptions;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesClient;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesOptions;
import jakarta.annotation.Resource;
import org.spring.aicloud.util.ResponseEntity;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.success(result);
    }

    /**
     * 绘画
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
        return ResponseEntity.success(result);
    }
}
