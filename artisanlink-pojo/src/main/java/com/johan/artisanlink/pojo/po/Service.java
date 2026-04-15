package com.johan.artisanlink.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体类
 */
@Data
@TableName("service")
public class Service implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
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
    private String detailImages;
    
    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
