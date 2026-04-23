package com.johan.artisanlink.server.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.dto.ServiceQueryDTO;
import com.johan.artisanlink.pojo.dto.ServiceSaveDTO;
import com.johan.artisanlink.pojo.po.ServiceCategory;
import com.johan.artisanlink.server.service.ServiceManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务管理模块
 */
@Slf4j
@RestController("adminServiceController")
@RequestMapping("/admin/service")
public class ServiceController {
    
    @Autowired
    private ServiceManagementService serviceManagementService;

    /**
     * 服务列表查询
     */
    @GetMapping("/list")
    public Result<Page<com.johan.artisanlink.pojo.po.Service>> list(ServiceQueryDTO queryDTO) {
        log.info("服务列表查询: {}", queryDTO);
        Page<com.johan.artisanlink.pojo.po.Service> page = serviceManagementService.pageQuery(queryDTO);
        return Result.success(page);
    }

    /**
     * 新增服务
     */
    @PostMapping("/save")
    public Result save(@RequestBody ServiceSaveDTO saveDTO) {
        log.info("新增服务: {}", saveDTO);
        serviceManagementService.save(saveDTO);
        return Result.success();
    }

    /**
     * 修改服务
     */
    @PutMapping("/update")
    public Result update(@RequestBody com.johan.artisanlink.pojo.po.Service service) {
        log.info("修改服务: {}", service);
        serviceManagementService.update(service);
        return Result.success();
    }

    /**
     * 服务详情查询
     */
    @GetMapping("/{id}")
    public Result<com.johan.artisanlink.pojo.po.Service> getById(@PathVariable Long id) {
        log.info("查询服务详情，ID: {}", id);
        com.johan.artisanlink.pojo.po.Service service = serviceManagementService.getById(id);
        return Result.success(service);
    }

    /**
     * 服务分类列表
     */
    @GetMapping("/category/list")
    public Result<List<ServiceCategory>> getCategoryList(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String name) {
        log.info("查询服务分类列表: merchantId={}, name={}", merchantId, name);
        List<ServiceCategory> list = serviceManagementService.getCategoryList(merchantId, name);
        return Result.success(list);
    }
}
