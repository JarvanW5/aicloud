package org.spring.aicloud.mapper;

import org.apache.ibatis.annotations.Select;
import org.spring.aicloud.entity.Discuss;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author JarvanW
 * @since 2024-08-08
 */
public interface DiscussMapper extends BaseMapper<Discuss> {

    @Select("UPDATE discuss SET readcount=readcount+1 WHERE did=#{did}")
    int updateReadcount(@RequestParam("did") Long did);

}
