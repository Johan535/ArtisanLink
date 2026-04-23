package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.pojo.dto.CustomerQueryDTO;
import com.johan.artisanlink.pojo.po.Customer;
import com.johan.artisanlink.server.mapper.CustomerMapper;
import com.johan.artisanlink.server.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 客户管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerMapper customerMapper;

    /**
     * 分页查询客户列表
     */
    @Override
    public Page<Customer> pageQuery(CustomerQueryDTO queryDTO) {
        // 构建分页对象
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getMerchantId() != null, Customer::getMerchantId, queryDTO.getMerchantId())
                .like(StringUtils.hasText(queryDTO.getName()), Customer::getName, queryDTO.getName())
                .like(StringUtils.hasText(queryDTO.getPhone()), Customer::getPhone, queryDTO.getPhone())
                .eq(queryDTO.getMemberLevel() != null, Customer::getMemberLevel, queryDTO.getMemberLevel())
                .orderByDesc(Customer::getCreateTime);
        
        // 执行分页查询
        return customerMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Customer getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    @Override
    public void save(Customer customer) {
        customerMapper.insert(customer);
    }

    @Override
    public void update(Customer customer) {
        if (customer.getId() == null) {
            throw new BusinessException("客户ID不能为空");
        }
        getById(customer.getId());
        customerMapper.updateById(customer);
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        customerMapper.deleteById(id);
    }
}
