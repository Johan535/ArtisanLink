package com.johan.artisanlink.server.service;

import com.johan.artisanlink.common.result.PageResult;
import com.johan.artisanlink.pojo.dto.StaffSaveDTO;

/**
 * 员工管理接口
 */
public interface StaffService {

    // 新增员工
    void save(StaffSaveDTO staffSaveDTO);

    // 分页查询员工
    PageResult page(StaffSaveDTO staffSaveDTO);
}
