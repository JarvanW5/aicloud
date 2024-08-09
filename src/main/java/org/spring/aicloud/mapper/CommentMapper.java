package org.spring.aicloud.mapper;

import org.apache.ibatis.annotations.Select;
import org.spring.aicloud.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.spring.aicloud.entity.vo.CommentVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-08
 */
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u.username FROM `comment` c " +
            "LEFT JOIN `user` u ON u.uid = c.uid " +
            "WHERE c.did = #{did} order by c.did desc")
    List<CommentVO> getCommentList(@RequestParam("did") Long did);
}
