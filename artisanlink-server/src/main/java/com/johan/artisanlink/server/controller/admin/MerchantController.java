package com.johan.artisanlink.server.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.dto.MerchantSaveDTO;
import com.johan.artisanlink.pojo.dto.MerchantUpdateDTO;
import com.johan.artisanlink.pojo.po.Merchant;
import com.johan.artisanlink.server.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商户管理模块
 */
@Slf4j
@RestController("adminMerchantController")
@RequestMapping("admin/merchant")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    //商户列表查询
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) Integer status){
        log.info("商户列表查询: name={}, status={}, pageNum={}, pageSize={}", name, status, pageNum, pageSize);
        Page<Merchant> page = merchantService.pageQuery(name, status, pageNum, pageSize);
        return Result.success(page);
                       }

    //新增商户
    @PostMapping("/save")
    public Result save(@RequestBody MerchantSaveDTO merchantSaveDTO){
        log.info("新增商户: {}", merchantSaveDTO);
        merchantService.save(merchantSaveDTO);
        return Result.success();
    }

    //修改商户
    @PutMapping("/update")
    public Result update(@RequestBody MerchantUpdateDTO merchantUpdateDTO){
        log.info("修改商户: {}", merchantUpdateDTO);
        merchantService.update(merchantUpdateDTO);
        return Result.success(merchantUpdateDTO);
    }

    //根据商户ID来进行商户信息查询（查询回显）
    @GetMapping("{id}")
    public Result<Merchant> getInfo(@PathVariable Long id){
        log.info("查询商户ID: {}", id);
        Merchant merchant = merchantService.getById(id);
        return Result.success(merchant);
    }

    @GetMapping("/info")
    public Result<Merchant> getCurrentInfo() {
        log.info("查询当前商户信息");
        return Result.success(merchantService.getCurrentMerchantInfo());
    }

    @PutMapping("/info")
    public Result updateCurrentInfo(@RequestBody MerchantUpdateDTO merchantUpdateDTO) {
        log.info("更新当前商户信息: {}", merchantUpdateDTO);
        merchantService.updateCurrentMerchantInfo(merchantUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        log.info("删除商户: {}", id);
        merchantService.deleteById(id);
        return Result.success();
    }
}
