package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.po.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户Mapper
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
