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

    public static class RejectOrderRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

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

    @PostMapping("/{id}/accept")
    public Result accept(@PathVariable Long id) {
        log.info("接单，ID: {}", id);
        orderService.accept(id);
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    public Result reject(@PathVariable Long id, @RequestBody(required = false) RejectOrderRequest request) {
        String reason = request == null ? null : request.getReason();
        log.info("拒单，ID: {}, reason: {}", id, reason);
        orderService.reject(id, reason);
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单，ID: {}", id);
        orderService.complete(id);
        return Result.success();
    }

    @PostMapping("/save")
    public Result save(@RequestBody Orders orders) {
        orderService.save(orders);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Orders orders) {
        orderService.update(orders);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
        return Result.success();
    }
}
