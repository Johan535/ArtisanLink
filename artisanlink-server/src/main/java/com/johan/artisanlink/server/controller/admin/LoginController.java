package com.johan.artisanlink.server.controller.admin;

import com.johan.artisanlink.pojo.dto.AdminLoginDTO;
import com.johan.artisanlink.pojo.vo.AdminLoginVO;
import com.johan.artisanlink.server.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* 登录模块
* */
@RestController("adminLoginController")
@RequestMapping("admin")
@Slf4j
public class LoginController {
    @Autowired
    private AdminService adminService;

    //登录
    @PostMapping("/login")
    public void login(AdminLoginDTO adminLoginDTO){
          adminService.login(adminLoginDTO);
    }

    //退出登录
    @PostMapping("logout")
    public void logout(){
         adminService.logout();
    }


}
