package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.po.ServiceCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务分类Mapper
 */
@Mapper
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory> {
}
