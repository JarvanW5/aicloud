package org.spring.aicloud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "cid", type = IdType.AUTO)
    private Long cid;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论用户id
     */
    private Long uid;

    /**
     * 讨论id
     */
    private Long did;

    /**
     * 回复id 0=顶级评论id
     */
    private Long pid;

    /**
     * 创建时间
     */
    private LocalDateTime createtime;

    /**
     * 修改时间
     */
    private LocalDateTime updatetime;

    /**
     * 点赞数
     */
    private Integer supportcount;

    private Integer state;


}
