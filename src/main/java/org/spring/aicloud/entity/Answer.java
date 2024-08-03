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
 * 大模型查询结果
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("answer")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "aid", type = IdType.AUTO)
    private Long aid;

    /**
     * 问题
     */
    private String title;

    /**
     * 答案
     */
    private String content;

    /**
     * 大模型类型，1=openai；2=通义千问；3=讯飞星火；4=文心一言；5=豆包；6=自定义
     */
    private Integer model;

    private LocalDateTime createtime;

    private LocalDateTime updatetime;

    private Long uid;

    /**
     * 生成类型，1=对话；2=绘图
     */
    private Integer type;


}
