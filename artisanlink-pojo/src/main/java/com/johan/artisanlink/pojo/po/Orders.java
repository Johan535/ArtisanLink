package com.johan.artisanlink.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Orders implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 客户手机号
     */
    private String customerPhone;
    
    /**
     * 服务ID
     */
    private Long serviceId;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 员工ID
     */
    private Long staffId;
    
    /**
     * 员工姓名
     */
    private String staffName;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 实付金额
     */
    private BigDecimal payAmount;
    
    /**
     * 预约时间
     */
    private LocalDateTime appointmentTime;
    
    /**
     * 订单状态 0-待支付 1-已支付 2-服务中 3-已完成 4-已取消 5-已退款
     */
    private Integer orderStatus;
    
    /**
     * 支付方式 1-微信 2-支付宝 3-现金
     */
    private Integer payMethod;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
