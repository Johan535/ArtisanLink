package com.johan.artisanlink.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.pojo.dto.ServiceQueryDTO;
import com.johan.artisanlink.pojo.dto.ServiceSaveDTO;
import com.johan.artisanlink.pojo.po.ServiceCategory;
import com.johan.artisanlink.server.mapper.ServiceCategoryMapper;
import com.johan.artisanlink.server.mapper.ServiceMapper;
import com.johan.artisanlink.server.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 服务管理服务实现类
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {
    
    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页查询服务列表
     */
    @Override
    public Page<com.johan.artisanlink.pojo.po.Service> pageQuery(ServiceQueryDTO queryDTO) {
        // 构建分页对象
        Page<com.johan.artisanlink.pojo.po.Service> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<com.johan.artisanlink.pojo.po.Service> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getMerchantId() != null, com.johan.artisanlink.pojo.po.Service::getMerchantId, queryDTO.getMerchantId())
                .eq(queryDTO.getCategoryId() != null, com.johan.artisanlink.pojo.po.Service::getCategoryId, queryDTO.getCategoryId())
                .like(StringUtils.hasText(queryDTO.getName()), com.johan.artisanlink.pojo.po.Service::getName, queryDTO.getName())
                .eq(queryDTO.getStatus() != null, com.johan.artisanlink.pojo.po.Service::getStatus, queryDTO.getStatus())
                .orderByDesc(com.johan.artisanlink.pojo.po.Service::getCreateTime);
        
        // 执行分页查询
        return serviceMapper.selectPage(page, queryWrapper);
    }

    /**
     * 新增服务
     */
    @Override
    public void save(ServiceSaveDTO saveDTO) {
        // 转换为实体类并保存
        com.johan.artisanlink.pojo.po.Service service = new com.johan.artisanlink.pojo.po.Service();
        BeanUtils.copyProperties(saveDTO, service);
        
        // 处理详情图片JSON数组
        if (saveDTO.getDetailImages() != null && !saveDTO.getDetailImages().isEmpty()) {
            try {
                service.setDetailImages(objectMapper.writeValueAsString(saveDTO.getDetailImages()));
            } catch (Exception e) {
                log.error("详情图片JSON序列化失败", e);
                throw new BusinessException("详情图片格式错误");
            }
        }
        
        // 默认状态为上架
        if (service.getStatus() == null) {
            service.setStatus(1);
        }
        
        // 默认销量为0
        if (service.getSales() == null) {
            service.setSales(0);
        }
        
        serviceMapper.insert(service);
        log.info("新增服务成功，服务ID：{}", service.getId());
    }

    /**
     * 修改服务
     */
    @Override
    public void update(com.johan.artisanlink.pojo.po.Service service) {
        // 检查服务是否存在
        com.johan.artisanlink.pojo.po.Service existService = serviceMapper.selectById(service.getId());
        if (existService == null) {
            throw new BusinessException("服务不存在");
        }
        
        serviceMapper.updateById(service);
        log.info("修改服务成功，服务ID：{}", service.getId());
    }

    /**
     * 查询服务详情
     */
    @Override
    public com.johan.artisanlink.pojo.po.Service getById(Long id) {
        com.johan.artisanlink.pojo.po.Service service = serviceMapper.selectById(id);
        if (service == null) {
            throw new BusinessException("服务不存在");
        }
        return service;
    }

    @Override
    public void delete(Long id) {
        com.johan.artisanlink.pojo.po.Service service = serviceMapper.selectById(id);
        if (service == null) {
            throw new BusinessException("服务不存在");
        }
        serviceMapper.deleteById(id);
    }

    /**
     * 查询服务分类列表
     */
    @Override
    public List<ServiceCategory> getCategoryList(Long merchantId, String name) {
        LambdaQueryWrapper<ServiceCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(merchantId != null, ServiceCategory::getMerchantId, merchantId)
                .like(StringUtils.hasText(name), ServiceCategory::getName, name)
                .orderByAsc(ServiceCategory::getSort);
        
        return serviceCategoryMapper.selectList(queryWrapper);
    }
}
