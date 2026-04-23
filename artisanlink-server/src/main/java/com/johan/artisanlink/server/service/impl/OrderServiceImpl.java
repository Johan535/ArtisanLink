package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.pojo.dto.OrderQueryDTO;
import com.johan.artisanlink.pojo.po.Orders;
import com.johan.artisanlink.server.mapper.OrdersMapper;
import com.johan.artisanlink.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 订单管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrdersMapper ordersMapper;

    /**
     * 分页查询订单列表
     */
    @Override
    public Page<Orders> pageQuery(OrderQueryDTO queryDTO) {
        // 构建分页对象
        Page<Orders> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getMerchantId() != null, Orders::getMerchantId, queryDTO.getMerchantId())
                .like(StringUtils.hasText(queryDTO.getOrderNo()), Orders::getOrderNo, queryDTO.getOrderNo())
                .like(StringUtils.hasText(queryDTO.getCustomerPhone()), Orders::getCustomerPhone, queryDTO.getCustomerPhone())
                .eq(queryDTO.getOrderStatus() != null, Orders::getOrderStatus, queryDTO.getOrderStatus())
                .orderByDesc(Orders::getCreateTime);
        
        // 执行分页查询
        return ordersMapper.selectPage(page, queryWrapper);
    }

    /**
     * 查询订单详情
     */
    @Override
    public Orders getById(Long id) {
        Orders orders = ordersMapper.selectById(id);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }
        return orders;
    }

    @Override
    public void accept(Long id) {
        Orders orders = getById(id);
        if (orders.getOrderStatus() == null || orders.getOrderStatus() != 1) {
            throw new BusinessException("当前订单状态不可接单");
        }
        orders.setOrderStatus(2);
        orders.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(orders);
    }

    @Override
    public void reject(Long id, String reason) {
        Orders orders = getById(id);
        if (orders.getOrderStatus() == null || orders.getOrderStatus() > 2) {
            throw new BusinessException("当前订单状态不可拒单");
        }
        orders.setOrderStatus(4);
        orders.setRemark(reason);
        orders.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(orders);
    }

    @Override
    public void complete(Long id) {
        Orders orders = getById(id);
        if (orders.getOrderStatus() == null || orders.getOrderStatus() != 2) {
            throw new BusinessException("当前订单状态不可完结");
        }
        orders.setOrderStatus(3);
        orders.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(orders);
    }

    @Override
    public void save(Orders orders) {
        ordersMapper.insert(orders);
    }

    @Override
    public void update(Orders orders) {
        if (orders.getId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        getById(orders.getId());
        ordersMapper.updateById(orders);
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        ordersMapper.deleteById(id);
    }
}
