package com.johan.artisanlink.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
@TableName("staff")
public class Staff implements Serializable {
    
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
     * 技能标签（JSON数组）
     */
    private String skillTags;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 状态 0-离职 1-在职
     */
    private Integer status;
    
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
