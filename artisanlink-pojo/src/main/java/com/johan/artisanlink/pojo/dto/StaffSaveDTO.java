package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 员工保存DTO
 */
@Data
public class StaffSaveDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 员工姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 入职时间
     */
    private LocalDate entryTime;
    
    /**
     * 技能标签
     */
    private List<String> skillTags;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 备注
     */
    private String remark;
}
