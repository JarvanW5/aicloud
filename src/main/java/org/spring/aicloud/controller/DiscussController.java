package org.spring.aicloud.controller;

/**
 * @Author: JarvanW
 * @Date: 2024/8/8
 * @Description:
 * @Requirements:
 */

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Discuss;
import org.spring.aicloud.entity.User;
import org.spring.aicloud.entity.vo.CommentVO;
import org.spring.aicloud.entity.vo.DiscussVO;
import org.spring.aicloud.service.ICommentService;
import org.spring.aicloud.service.IDiscussService;
import org.spring.aicloud.service.IUserService;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 讨论表控制器
 */
@RestController
@RequestMapping("/discuss")
public class DiscussController {

    @Resource
    private IDiscussService discussService;

    @Resource
    private ThreadPoolTaskExecutor threadPool;

    @Resource
    private IUserService userService;

    @Resource
    private ICommentService commentService;

    @RequestMapping("/test")
    public ResponseEntity test() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }, threadPool);

        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 2;
        }, threadPool);

        CompletableFuture<Integer> task3 = CompletableFuture.allOf(task1, task2)
                .handle((res, e) -> {
                    try {
                        return task1.get() + task2.get();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    } catch (ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        Integer result = task3.get();
        System.out.println(result);

        return ResponseEntity.success("Result:" + result);
    }

    /**
     * 查询讨论详情
     */
    @RequestMapping("/detail")
    public ResponseEntity detail(Long did) throws ExecutionException, InterruptedException {
        if (did == null || did <= 0) return ResponseEntity.error("参数错误！");
        Discuss discuss = discussService.getById(did);
        if (discuss != null && discuss.getDid() > 0) {
            // 添加阅读量
            threadPool.submit(() -> {
                // 1、更新数据
                discussService.updateReadcount(did);

                // 2、返回对象阅读数+1
                discuss.setReadcount(discuss.getReadcount() + 1);
            });

            // 任务1：查询 discuss 中的 username
            CompletableFuture<DiscussVO> task1 = CompletableFuture.supplyAsync(() -> {
                // 对象转换
                DiscussVO discussVO = BeanUtil.toBean(discuss, DiscussVO.class);
                User user = userService.getById(discuss.getUid());
                if (user != null && user.getUid() > 0) {
                    discussVO.setUsername(user.getUsername());
                }
                return discussVO;
            }, threadPool);

            // 任务2：查询 discuss 所对应的 comment 列表
            CompletableFuture<List<CommentVO>> task2 = CompletableFuture.supplyAsync(() -> {
                return commentService.getCommentList(did);
            }, threadPool);
            CompletableFuture.allOf(task1, task2);
            HashMap<String, Object> result = new HashMap<>();
            result.put("discuss", task1.get());
            result.put("commentList", task2.get());
            return ResponseEntity.success(result);
        }
        return ResponseEntity.error("该讨论不存在，请重试！");
    }


    /**
     * 添加话题讨论
     */

    @RequestMapping("/add")
    public ResponseEntity add(@Validated Discuss discuss) {

        discuss.setUid(SecurityUtil.getCurrentUser().getUid());
        boolean save = discussService.save(discuss);
        if (save) {
            return ResponseEntity.success(save);
        }
        return ResponseEntity.error("保存失败，请重试！");
    }

    /**
     * 获取我的讨论列表
     */

    @RequestMapping("/mylist")
    public ResponseEntity getMylist() {
        return ResponseEntity.success(discussService.list(
                Wrappers.lambdaQuery(Discuss.class)
                        .eq(Discuss::getUid, SecurityUtil.getCurrentUser().getUid())
                        .orderByDesc(Discuss::getDid)
        ));
    }


    /**
     * 讨论删除
     */
    @RequestMapping("/delete")
    public ResponseEntity delete(Long did) {
        if (did == null || did <= 0) return ResponseEntity.error("参数错误！");
        boolean result = discussService.remove(
                Wrappers.lambdaQuery(Discuss.class)
                        .eq(Discuss::getDid, did)
                        .eq(Discuss::getUid, SecurityUtil.getCurrentUser().getUid())
        );
        if (result) {
            return ResponseEntity.success(result);
        }
        return ResponseEntity.error("删除失败，请重试！");
    }
}
