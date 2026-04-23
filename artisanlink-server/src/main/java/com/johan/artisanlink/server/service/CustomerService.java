package com.johan.artisanlink.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.pojo.dto.CustomerQueryDTO;
import com.johan.artisanlink.pojo.po.Customer;

/**
 * 客户管理服务接口
 */
public interface CustomerService {
    
    /**
     * 分页查询客户列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<Customer> pageQuery(CustomerQueryDTO queryDTO);

    Customer getById(Long id);

    void save(Customer customer);

    void update(Customer customer);

    void deleteById(Long id);
}
