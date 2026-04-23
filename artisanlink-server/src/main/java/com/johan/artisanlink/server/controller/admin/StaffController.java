package com.johan.artisanlink.server.controller.admin;

import com.johan.artisanlink.common.result.PageResult;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.dto.StaffSaveDTO;
import com.johan.artisanlink.server.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理模块
 */
@Slf4j
@RestController("adminStaffController")
@RequestMapping("admin/staff")
public class StaffController {
    @Autowired
    private StaffService staffService;

    // 列表查询员工
    @GetMapping("/list")
    public Result page(StaffSaveDTO staffSaveDTO){
        log.info("分页查询员工信息，参数：page：",staffSaveDTO);
        PageResult pageResult =  staffService.page(staffSaveDTO);
        return Result.success(pageResult);
    }

    // 新增员工
    @PostMapping("/save")
    public Result save(@RequestBody StaffSaveDTO staffSaveDTO){
        log.info("新增员工：{}",staffSaveDTO);
        staffService.save(staffSaveDTO);
        return Result.success();
    }




}


