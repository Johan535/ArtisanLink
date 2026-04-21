package com.johan.artisanlink.server.controller.admin;

import com.johan.artisanlink.server.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商户管理模块
 */
@Slf4j
@RestController("adminMerchantController")
@RequestMapping("admin/merchant")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    //TODO 商户列表查询
    @GetMapping("/list")
    public Object list() {

    }

    //TODO 新增商户
    @PostMapping("/save")

    //TODO 修改商户
    @PutMapping("/update")

    //TODO 商户详情查询
    @GetMapping("{id}")
}
