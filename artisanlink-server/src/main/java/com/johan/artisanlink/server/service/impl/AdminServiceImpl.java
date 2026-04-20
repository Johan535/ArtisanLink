package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.common.util.JwtUtil;
import com.johan.artisanlink.pojo.dto.AdminLoginDTO;
import com.johan.artisanlink.pojo.po.Admin;
import com.johan.artisanlink.pojo.vo.AdminLoginVO;
import com.johan.artisanlink.server.mapper.AdminMapper;
import com.johan.artisanlink.server.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public AdminLoginVO login(AdminLoginDTO loginDTO) {
        // 1. 参数校验
        if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        
        // TODO: 验证码校验（后续实现）
        
        // 2. 查询管理员
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, loginDTO.getUsername());
        Admin admin = adminMapper.selectOne(queryWrapper);
        
        // 3. 管理员不存在
        if (admin == null) {
            log.warn("管理员登录失败，用户名不存在：{}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 4. 验证密码（使用BCrypt）
        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            log.warn("管理员登录失败，密码错误：{}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 5. 检查管理员状态
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            log.warn("管理员登录失败，账号已被禁用：{}", loginDTO.getUsername());
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        
        // 6. 生成Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        String token = JwtUtil.generateToken(claims);
        
        // 7. 构建响应
        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setToken(token);
        
        AdminLoginVO.AdminInfoVO adminInfoVO = new AdminLoginVO.AdminInfoVO();
        adminInfoVO.setId(admin.getId());
        adminInfoVO.setUsername(admin.getUsername());
        adminInfoVO.setName(admin.getName());
        adminInfoVO.setPhone(admin.getPhone());
        adminInfoVO.setAvatar(admin.getAvatar());
        loginVO.setAdminInfo(adminInfoVO);
        
        log.info("管理员登录成功，ID：{}，用户名：{}", admin.getId(), admin.getUsername());
        return loginVO;
    }
    
    @Override
    public void logout() {
        // 由于使用JWT无状态token，退出登录由前端删除token即可
        // 如果需要实现token黑名单机制，可以在这里将token加入Redis黑名单
        log.info("管理员退出登录");
    }
}
