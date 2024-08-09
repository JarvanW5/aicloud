package org.spring.aicloud.service.impl;

import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Discuss;
import org.spring.aicloud.mapper.DiscussMapper;
import org.spring.aicloud.service.IDiscussService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-08
 */
@Service
public class DiscussServiceImpl extends ServiceImpl<DiscussMapper, Discuss> implements IDiscussService {

    @Resource
    private DiscussMapper discussMapper;

    @Override
    public int updateReadcount(Long did) {
        return discussMapper.updateReadcount(did);
    }
}
