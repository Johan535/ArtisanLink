package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务项目查询DTO
 */
@Data
public class ServiceQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 服务名称
     */
    private String name;
    
    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页显示记录数
     */
    private Integer pageSize = 10;
}
