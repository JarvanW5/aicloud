package org.spring.aicloud;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AicloudApplicationTests {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        String password = "123456";

        String password1 = passwordEncoder.encode(password);
        String password2 = passwordEncoder.encode(password);
        String password3 = passwordEncoder.encode(password);


        System.out.println(password1);
        System.out.println(password2);
        System.out.println(password3);

        String inputPassword = "123";
        String inputPassword2 = "123456";

        System.out.println("result:" + passwordEncoder.matches(inputPassword, password1));
    }

}
