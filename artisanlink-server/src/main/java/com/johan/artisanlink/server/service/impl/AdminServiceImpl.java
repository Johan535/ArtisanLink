package com.johan.artisanlink.server.service.impl;

import com.johan.artisanlink.common.constant.MessageConstant;
import com.johan.artisanlink.common.constant.StatusConstant;
import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.pojo.dto.AdminLoginDTO;
import com.johan.artisanlink.pojo.po.Admin;
import com.johan.artisanlink.server.mapper.AdminMapper;
import com.johan.artisanlink.server.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    // 密码加密
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     *
     * @param loginDTO 登录信息
     * @return
     * @throws Exception
     */
    @Override
    public Admin login(AdminLoginDTO loginDTO) throws Exception{
        // 1. 参数校验
        if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        
        // 2.TODO: 验证码校验（后续实现）
        
        // 3. 根据用户名查询数据库中的数据
        Admin admin = adminMapper.getByUsername(loginDTO.getUsername());

        // 处理各种异常情况（如果用户名不存在、密码不对、账号被锁定）
        if(admin == null){
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        if(!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())){
            // 密码错误
            throw new  BusinessException(MessageConstant.PASSWORD_ERROR);
        }

        //账号被锁定
        if(admin.getStatus() == StatusConstant.DISABLE){
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return admin;
    }
    
    @Override
    public void logout() {
        // 由于使用JWT无状态token，退出登录由前端删除token即可
        // 如果需要实现token黑名单机制，可以在这里将token加入Redis黑名单
        log.info("管理员退出登录");
    }
}
