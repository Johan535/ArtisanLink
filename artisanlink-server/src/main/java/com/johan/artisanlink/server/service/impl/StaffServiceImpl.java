package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.johan.artisanlink.common.constant.StatusConstant;
import com.johan.artisanlink.common.result.PageResult;
import com.johan.artisanlink.pojo.dto.StaffSaveDTO;
import com.johan.artisanlink.pojo.po.Staff;
import com.johan.artisanlink.server.mapper.StaffMapper;
import com.johan.artisanlink.server.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 员工管理接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;

    //新增员工
    @Override
    public void save(StaffSaveDTO staffSaveDTO) {
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffSaveDTO, staff);
        staff.setStatus(StatusConstant.ENABLE);
        staffMapper.insert(staff);
    }

    //分页查询员工
    @Override
    public PageResult page(StaffSaveDTO staffSaveDTO) {
        //1.设置分页参数
        PageHelper.startPage(staffSaveDTO.getPage(), staffSaveDTO.getPageSize());

        //2.调用mapper接口查询
        List<Staff> staffList = staffMapper.pageQuery(staffSaveDTO);

        //3.封装成pageResult并返回
        Page<Staff> page = (Page<Staff>) staffList; // 将查询结果封装成page对象
        // 第一个参数是总页数，第二个参数是当前页的数据
        return new PageResult(page.getPages(),page.getRecords());
    }
}
