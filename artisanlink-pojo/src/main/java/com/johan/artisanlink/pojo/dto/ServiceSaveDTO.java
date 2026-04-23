package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 服务项目保存DTO
 */
@Data
public class ServiceSaveDTO implements Serializable {
    
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
     * 服务描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 时长（分钟）
     */
    private Integer duration;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 详情图片（JSON数组）
     */
    private List<String> detailImages;
    
    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;
}
