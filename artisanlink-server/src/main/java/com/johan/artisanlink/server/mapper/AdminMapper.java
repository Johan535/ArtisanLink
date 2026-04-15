package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.po.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
