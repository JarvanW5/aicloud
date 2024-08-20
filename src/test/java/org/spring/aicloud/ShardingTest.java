package org.spring.aicloud;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.service.IAnswerService;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: JarvanW
 * @Date: 2024/8/20
 * @Description:
 * @Requirements:
 */

@SpringBootTest
public class ShardingTest {

    @Resource
    private IAnswerService answerService;


    @Test
    public void insertTest() {
        for (long i = 1; i < 5; i++) {
            Answer answer = new Answer();
            answer.setTitle("test");
            answer.setContent("test");
            answer.setType(1);
            answer.setModel(1);
            answer.setUid(i);
            answerService.save(answer);

        }
    }


    @Test
    public void selectTest() {
        System.out.println(answerService.getById(3L));
        System.out.println("--------------------------");
        System.out.println(answerService.getById(1032447406651736064L));
    }


}
