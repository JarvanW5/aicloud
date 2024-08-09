package org.spring.aicloud.service;

import org.spring.aicloud.entity.Discuss;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-08
 */
public interface IDiscussService extends IService<Discuss> {
    int updateReadcount(Long did);
}
