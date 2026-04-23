package com.johan.artisanlink.server.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.dto.OrderQueryDTO;
import com.johan.artisanlink.pojo.po.Orders;
import com.johan.artisanlink.server.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理模块
 */
@Slf4j
@RestController("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    /**
     * 订单列表查询
     */
    @GetMapping("/list")
    public Result<Page<Orders>> list(OrderQueryDTO queryDTO) {
        log.info("订单列表查询: {}", queryDTO);
        Page<Orders> page = orderService.pageQuery(queryDTO);
        return Result.success(page);
    }

    /**
     * 订单详情查询
     */
    @GetMapping("/{id}")
    public Result<Orders> getById(@PathVariable Long id) {
        log.info("查询订单详情，ID: {}", id);
        Orders orders = orderService.getById(id);
        return Result.success(orders);
    }
}
