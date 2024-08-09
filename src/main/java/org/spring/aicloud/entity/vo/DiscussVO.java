package org.spring.aicloud.entity.vo;

import lombok.Data;
import org.spring.aicloud.entity.Discuss;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: JarvanW
 * @Date: 2024/8/9
 * @Description:
 * @Requirements:
 */
@Data
public class DiscussVO extends Discuss implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String username;
}
