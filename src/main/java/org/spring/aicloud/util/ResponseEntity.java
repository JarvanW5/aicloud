package org.spring.aicloud.util;

/**
 * @Author: JarvanW
 * @Date: 2024/7/30
 * @Description:
 * @Requirements:
 */


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回对象 （进行 Ajax 响应）
 */
@Data
public class ResponseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 4581593792717613764L;
    private int code;   // 状态码
    private String msg;    // 状态信息
    private Object data;    // 数据


    public static ResponseEntity success(Object data) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(200);
        responseEntity.setMsg("success");
        responseEntity.setData(data);
        return responseEntity;
    }

    public static ResponseEntity error(String msg) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(500);
        responseEntity.setMsg(msg);
        responseEntity.setData(null);
        return responseEntity;
    }

}
