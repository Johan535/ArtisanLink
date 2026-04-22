package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.dto.StaffSaveDTO;
import com.johan.artisanlink.pojo.po.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 员工Mapper
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {

    //员工分页查询信息
    List<Staff> pageQuery(StaffSaveDTO staffSaveDTO);
}
