package com.johan.artisanlink.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 客户手机号
     */
    private String customerPhone;
    
    /**
     * 订单状态 0-待支付 1-已支付 2-服务中 3-已完成 4-已取消 5-已退款
     */
    private Integer orderStatus;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页显示记录数
     */
    private Integer pageSize = 10;
}
