package com.johan.artisanlink.server.service;

import com.johan.artisanlink.pojo.dto.AdminLoginDTO;
import com.johan.artisanlink.pojo.po.Admin;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 管理员登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    Admin login(AdminLoginDTO loginDTO) throws Exception;
    
    /**
     * 退出登录
     */
    void logout();
}
