package org.spring.aicloud.util;

import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MinIoUtilTest {

    @Resource
    private MinIoUtil minIoUtil;

    @Test
    void upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File("D:\\JarvanW\\workplace\\java_work\\develop\\test\\cat.png");
        InputStream inputStream = new FileInputStream(file);
        System.out.println(minIoUtil.upload("cat.png",inputStream,"image/png"));
    }
}