package com.johan.artisanlink.server.controller.admin;

import com.johan.artisanlink.common.constant.CommonConstant;
import com.johan.artisanlink.common.constant.JwtClaimsConstant;
import com.johan.artisanlink.common.properties.JwtProperties;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.common.util.JwtUtil;
import com.johan.artisanlink.pojo.dto.AdminLoginDTO;
import com.johan.artisanlink.pojo.po.Admin;
import com.johan.artisanlink.pojo.vo.AdminLoginVO;
import com.johan.artisanlink.server.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
* 管理员登录模块
* */
@RestController("adminLoginController")
@RequestMapping("admin")
@Slf4j
public class LoginController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtProperties jwtProperties;

    //登录
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) throws Exception { //@RequestBody: 将json数据映射为对象
        log.info("登录参数: {}", adminLoginDTO);
        
        // 创建对象实例admin，登录验证，获取管理员信息
        Admin admin = adminService.login(adminLoginDTO);

        // 登陆成功后，生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, admin.getId()); // 添加员工ID
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(), // 密钥
                jwtProperties.getAdminTtl(), // 过期时间
                claims); //设置信息

        // 封装返回结果
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        AdminLoginVO.AdminInfoVO adminInfoVO = new AdminLoginVO.AdminInfoVO();
        adminInfoVO.setId(admin.getId());
        adminInfoVO.setUsername(admin.getUsername());
        adminInfoVO.setName(admin.getName());
        adminInfoVO.setPhone(admin.getPhone());
        adminInfoVO.setAvatar(admin.getAvatar());
        adminLoginVO.setAdminInfo(adminInfoVO);
        adminLoginVO.setToken(token); // 存入令牌

        return Result.success(adminLoginVO); // 返回封装结果
    }

    //退出登录
    @PostMapping("logout")
    public Result<String> logout(){
        return Result.success();
    }


}
