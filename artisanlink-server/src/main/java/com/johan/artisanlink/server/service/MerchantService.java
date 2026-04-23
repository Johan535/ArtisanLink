package com.johan.artisanlink.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.pojo.dto.MerchantSaveDTO;
import com.johan.artisanlink.pojo.dto.MerchantUpdateDTO;
import com.johan.artisanlink.pojo.po.Merchant;

/**
 * 商户服务接口
 */
public interface MerchantService {
    
    /**
     * 分页查询商户列表
     * @param name 商户名称
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Merchant> pageQuery(String name, Integer status, Integer pageNum, Integer pageSize);
    
    /**
     * 新增商户
     * @param saveDTO 商户信息
     */
    void save(MerchantSaveDTO saveDTO);
    
    /**
     * 修改商户
     * @param updateDTO 商户信息
     */
    void update(MerchantUpdateDTO updateDTO);
    
    /**
     * 查询商户详情
     * @param id 商户ID
     * @return 商户信息
     */
    Merchant getById(Long id);

    /**
     * 获取当前商户信息（管理端首页信息）
     */
    Merchant getCurrentMerchantInfo();

    /**
     * 更新当前商户信息
     */
    void updateCurrentMerchantInfo(MerchantUpdateDTO updateDTO);
}
