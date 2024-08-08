package org.spring.aicloud.service.impl;

import org.spring.aicloud.entity.Comment;
import org.spring.aicloud.mapper.CommentMapper;
import org.spring.aicloud.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-08
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
