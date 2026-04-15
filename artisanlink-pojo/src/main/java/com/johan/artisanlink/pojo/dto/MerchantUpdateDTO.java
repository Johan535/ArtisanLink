package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商户更新DTO
 */
@Data
public class MerchantUpdateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商户名称
     */
    private String name;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 营业时间
     */
    private String businessHours;
    
    /**
     * Logo图片URL
     */
    private String logo;
    
    /**
     * 状态 0-停业 1-营业
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
