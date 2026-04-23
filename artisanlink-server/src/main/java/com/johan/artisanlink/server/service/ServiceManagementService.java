package com.johan.artisanlink.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.pojo.dto.ServiceQueryDTO;
import com.johan.artisanlink.pojo.dto.ServiceSaveDTO;
import com.johan.artisanlink.pojo.po.ServiceCategory;

import java.util.List;

/**
 * 服务管理服务接口
 */
public interface ServiceManagementService {
    
    /**
     * 分页查询服务列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<com.johan.artisanlink.pojo.po.Service> pageQuery(ServiceQueryDTO queryDTO);
    
    /**
     * 新增服务
     * @param saveDTO 服务信息
     */
    void save(ServiceSaveDTO saveDTO);
    
    /**
     * 修改服务
     * @param service 服务信息
     */
    void update(com.johan.artisanlink.pojo.po.Service service);
    
    /**
     * 查询服务详情
     * @param id 服务ID
     * @return 服务信息
     */
    com.johan.artisanlink.pojo.po.Service getById(Long id);

    /**
     * 删除服务
     * @param id 服务ID
     */
    void delete(Long id);
    
    /**
     * 查询服务分类列表
     * @param merchantId 商户ID
     * @param name 分类名称
     * @return 分类列表
     */
    List<ServiceCategory> getCategoryList(Long merchantId, String name);
}
