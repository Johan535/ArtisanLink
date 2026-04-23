package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户查询DTO
 */
@Data
public class CustomerQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 客户姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 会员等级 0-普通 1-银卡 2-金卡 3-钻石
     */
    private Integer memberLevel;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页显示记录数
     */
    private Integer pageSize = 10;
}
