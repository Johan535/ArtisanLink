package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.po.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
