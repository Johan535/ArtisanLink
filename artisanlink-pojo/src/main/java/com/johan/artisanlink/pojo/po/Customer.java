package com.johan.artisanlink.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户实体类
 */
@Data
@TableName("customer")
public class Customer implements Serializable {
    
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
     * 客户姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDateTime birthday;
    
    /**
     * 会员等级 0-普通 1-银卡 2-金卡 3-钻石
     */
    private Integer memberLevel;
    
    /**
     * 积分
     */
    private Integer points;
    
    /**
     * 余额
     */
    private java.math.BigDecimal balance;
    
    /**
     * 头像URL
     */
    private String avatar;
    
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
