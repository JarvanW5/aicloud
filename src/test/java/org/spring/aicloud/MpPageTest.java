package org.spring.aicloud;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.spring.aicloud.entity.Discuss;
import org.spring.aicloud.service.IDiscussService;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: JarvanW
 * @Date: 2024/8/13
 * @Description:
 * @Requirements:
 */

@SpringBootTest
public class MpPageTest {

    @Resource
    private IDiscussService discussService;

    @Test
    void test() {

        Page<Discuss> page = discussService.page(new Page<>(4, 2),
                Wrappers.lambdaQuery(Discuss.class).orderByDesc(Discuss::getDid));
        System.out.println(page);

    }
}
