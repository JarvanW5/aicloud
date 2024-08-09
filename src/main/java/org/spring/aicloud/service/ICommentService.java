package org.spring.aicloud.service;

import org.spring.aicloud.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.spring.aicloud.entity.vo.CommentVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-08
 */
public interface ICommentService extends IService<Comment> {

    public List<CommentVO> getCommentList(Long did);

}
