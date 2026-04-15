package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 商户保存DTO
 */
@Data
public class MerchantSaveDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
     * 备注
     */
    private String remark;
}
