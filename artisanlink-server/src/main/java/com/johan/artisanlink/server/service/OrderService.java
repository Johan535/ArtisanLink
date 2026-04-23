package com.johan.artisanlink.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.pojo.dto.OrderQueryDTO;
import com.johan.artisanlink.pojo.po.Orders;

/**
 * 订单管理服务接口
 */
public interface OrderService {
    
    /**
     * 分页查询订单列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<Orders> pageQuery(OrderQueryDTO queryDTO);
    
    /**
     * 查询订单详情
     * @param id 订单ID
     * @return 订单信息
     */
    Orders getById(Long id);
}
