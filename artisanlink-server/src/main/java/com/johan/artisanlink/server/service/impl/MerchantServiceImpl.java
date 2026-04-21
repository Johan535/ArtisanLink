package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.pojo.dto.MerchantSaveDTO;
import com.johan.artisanlink.pojo.dto.MerchantUpdateDTO;
import com.johan.artisanlink.pojo.po.Merchant;
import com.johan.artisanlink.server.mapper.MerchantMapper;
import com.johan.artisanlink.server.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 商户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    
    private final MerchantMapper merchantMapper;

    // 分页查询
    @Override
    public Page<Merchant> pageQuery(String name, Integer status, Integer pageNum, Integer pageSize) {
        // 构建分页对象
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name), Merchant::getName, name)
                .eq(status != null, Merchant::getStatus, status)
                .orderByDesc(Merchant::getCreateTime);
        
        // 执行分页查询
        return merchantMapper.selectPage(page, queryWrapper);
    }

    // 新增商户
    @Override
    public void save(MerchantSaveDTO saveDTO) {
        // 检查商户名称是否已存在
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getName, saveDTO.getName());
        Long count = merchantMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("商户名称已存在");
        }
        
        // 转换为实体类并保存
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(saveDTO, merchant);
        merchant.setStatus(1); // 默认启用
        
        merchantMapper.insert(merchant);
        log.info("新增商户成功，商户ID：{}", merchant.getId());
    }

    // 修改商户
    @Override
    public void update(MerchantUpdateDTO updateDTO) {
        // 检查商户是否存在
        Merchant existMerchant = merchantMapper.selectById(updateDTO.getId());
        if (existMerchant == null) {
            throw new BusinessException("商户不存在");
        }
        
        // 更新商户信息
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(updateDTO, merchant);
        
        merchantMapper.updateById(merchant);
        log.info("修改商户成功，商户ID：{}", updateDTO.getId());
    }

    // 根据ID查询商户
    @Override
    public Merchant getById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        return merchant;
    }
}
