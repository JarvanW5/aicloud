package org.spring.aicloud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author JarvanW
 * @since 2024-07-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "uid", type = IdType.ASSIGN_ID)
    private Long uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String photo;

    /**
     * 状态，预留字段
     */
    private Integer state;

    /**
     * 创建时间
     */
    private String createtime;

    /**
     * 修改时间
     */
    private String updatetime;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 每天可以使用大模型的次数
     */
    private String usecount;


}