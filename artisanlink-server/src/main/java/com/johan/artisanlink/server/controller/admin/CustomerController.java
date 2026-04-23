package com.johan.artisanlink.server.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.dto.CustomerQueryDTO;
import com.johan.artisanlink.pojo.po.Customer;
import com.johan.artisanlink.server.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理模块
 */
@Slf4j
@RestController("adminCustomerController")
@RequestMapping("/admin/customer")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;

    /**
     * 客户列表查询
     */
    @GetMapping("/list")
    public Result<Page<Customer>> list(CustomerQueryDTO queryDTO) {
        log.info("客户列表查询: {}", queryDTO);
        Page<Customer> page = customerService.pageQuery(queryDTO);
        return Result.success(page);
    }
}
