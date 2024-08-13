package org.spring.aicloud.controller;

/**
 * @Author: JarvanW
 * @Date: 2024/8/13
 * @Description:
 * @Requirements:
 */

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;

import org.spring.aicloud.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 字节大模型的对接
 */
@RestController
@RequestMapping("/doubao")
public class DoubaoController {

    @Value("${doubao.api-key}")
    private String apiKey;
    @Value("${doubao.url}")
    private String url;
    @Value("${doubao.model-id}")
    private String modelId;

    @Resource
    private IAnswerService answerService;


    /**
     * 对话功能
     */
    @RequestMapping("/chat")
    public ResponseEntity chat(String question) {
        if (!StringUtils.hasLength(question)) return ResponseEntity.error("请先输入问题：");

        // 1\ api-key，调用 url 地址
        ArkService service = ArkService.builder().apiKey(apiKey).baseUrl(url).build();

        // 2\ 构建问题对象
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(question)
                .build());
        // 3\ 创建对象进行对话
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)     // 接入点的id （并不是大模型的名字）
                .messages(messages)
                .build();

        // 4\ 得到对话的结果
        String result = service.createChatCompletion(request)
                .getChoices()
                .get(0)      // 获取 AI 大模型生成的第一条消息
                .getMessage()
                .getContent().toString();
        System.out.println("豆包大模型结果" + result);
        // 将对话信息存储到数据库
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(result);
        answer.setModel(AiModelEnum.DOUBAO.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        answer.setType(AiTypeEnum.CHAT.getValue());
        if (answerService.save(answer)) {
            return ResponseEntity.success(result);
        }

        return ResponseEntity.error("请求操作失败，请重试！");
    }
}
