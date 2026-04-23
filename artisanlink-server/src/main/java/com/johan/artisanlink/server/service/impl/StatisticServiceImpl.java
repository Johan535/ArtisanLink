package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.johan.artisanlink.pojo.po.Customer;
import com.johan.artisanlink.pojo.po.Orders;
import com.johan.artisanlink.pojo.vo.BusinessDataVO;
import com.johan.artisanlink.server.mapper.CustomerMapper;
import com.johan.artisanlink.server.mapper.OrdersMapper;
import com.johan.artisanlink.server.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final OrdersMapper ordersMapper;
    private final CustomerMapper customerMapper;

    @Override
    public BusinessDataVO merchantStatistics(Long merchantId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Orders> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(merchantId != null, Orders::getMerchantId, merchantId)
                .between(startTime != null && endTime != null, Orders::getCreateTime, startTime, endTime);
        List<Orders> orders = ordersMapper.selectList(orderQuery);

        int totalOrders = orders.size();
        int validOrderCount = (int) orders.stream().filter(o -> o.getOrderStatus() != null && o.getOrderStatus() == 3).count();

        BigDecimal turnover = orders.stream()
                .filter(o -> o.getOrderStatus() != null && o.getOrderStatus() == 3 && o.getPayAmount() != null)
                .map(Orders::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double completionRate = totalOrders == 0 ? 0D : BigDecimal.valueOf(validOrderCount)
                .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                .doubleValue();
        double unitPrice = validOrderCount == 0 ? 0D : turnover
                .divide(BigDecimal.valueOf(validOrderCount), 2, RoundingMode.HALF_UP)
                .doubleValue();

        LambdaQueryWrapper<Customer> customerQuery = new LambdaQueryWrapper<>();
        customerQuery.eq(merchantId != null, Customer::getMerchantId, merchantId)
                .between(startTime != null && endTime != null, Customer::getCreateTime, startTime, endTime);
        Integer newUsers = Math.toIntExact(customerMapper.selectCount(customerQuery));

        return BusinessDataVO.builder()
                .turnover(turnover.doubleValue())
                .validOrderCount(validOrderCount)
                .orderCompletionRate(completionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

}
