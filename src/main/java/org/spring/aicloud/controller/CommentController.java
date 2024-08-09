package org.spring.aicloud.controller;

/**
 * @Author: JarvanW
 * @Date: 2024/8/9
 * @Description:
 * @Requirements:
 */

import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Comment;
import org.spring.aicloud.service.ICommentService;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论表控制器
 */

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    /**
     * 添加评论
     */

    @RequestMapping("/add")
    public ResponseEntity add(@Validated Comment comment) {
        comment.setUid(SecurityUtil.getCurrentUser().getUid());

        boolean result = commentService.save(comment);

        if (result) {
            return ResponseEntity.success(result);
        }
        return ResponseEntity.error("添加失败，请重试！");
    }
}
