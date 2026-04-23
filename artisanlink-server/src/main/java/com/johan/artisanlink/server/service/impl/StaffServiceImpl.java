package com.johan.artisanlink.server.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.johan.artisanlink.common.exception.BusinessException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * 员工管理接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor // 自动注入
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    //新增员工
    @Override
    public void save(StaffSaveDTO staffSaveDTO) {
        Staff staff = new Staff();
        BeanUtils.copyProperties(staffSaveDTO, staff);
        if (staffSaveDTO.getSkillTags() != null) {
            try {
                staff.setSkillTags(OBJECT_MAPPER.writeValueAsString(staffSaveDTO.getSkillTags()));
            } catch (Exception e) {
                throw new BusinessException("技能标签格式错误");
            }
        }
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
        Page<Staff> page = (Page<Staff>) staffList;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void update(Long id, StaffSaveDTO staffSaveDTO) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw new BusinessException("员工不存在");
        }
        BeanUtils.copyProperties(staffSaveDTO, staff);
        if (staffSaveDTO.getSkillTags() != null) {
            try {
                staff.setSkillTags(OBJECT_MAPPER.writeValueAsString(staffSaveDTO.getSkillTags()));
            } catch (Exception e) {
                throw new BusinessException("技能标签格式错误");
            }
        }
        staffMapper.updateById(staff);
    }

    @Override
    public void delete(Long id) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw new BusinessException("员工不存在");
        }
        staffMapper.deleteById(id);
    }
}
