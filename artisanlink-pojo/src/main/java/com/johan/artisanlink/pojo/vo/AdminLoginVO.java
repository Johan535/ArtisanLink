package com.johan.artisanlink.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员登录响应VO
 */
@Data
public class AdminLoginVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 管理员信息
     */
    private AdminInfoVO adminInfo;
    
    @Data
    public static class AdminInfoVO implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 管理员ID
         */
        private Long id;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 姓名
         */
        private String name;
        
        /**
         * 手机号
         */
        private String phone;
        
        /**
         * 头像
         */
        private String avatar;
    }
}
