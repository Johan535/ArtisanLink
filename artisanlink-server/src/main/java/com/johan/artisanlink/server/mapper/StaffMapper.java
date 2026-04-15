package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.po.Staff;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工Mapper
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
}
