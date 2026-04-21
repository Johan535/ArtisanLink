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

    public AdminInfoVO getAdminInfo() {
        return adminInfo;
    }

    public void setAdminInfo(AdminInfoVO adminInfo) {
        this.adminInfo = adminInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

        public AdminInfoVO getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public AdminInfoVO getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public AdminInfoVO getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
