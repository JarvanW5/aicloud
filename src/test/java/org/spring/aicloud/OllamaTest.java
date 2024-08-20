package org.spring.aicloud;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: JarvanW
 * @Date: 2024/8/20
 * @Description:
 * @Requirements:
 */

@SpringBootTest
public class OllamaTest {
    @Resource
    private OllamaChatClient ollamaChatClient;

    @Test
    public void test() {
        String response = ollamaChatClient.call("你是谁");
        System.out.println(response);
    }
}
