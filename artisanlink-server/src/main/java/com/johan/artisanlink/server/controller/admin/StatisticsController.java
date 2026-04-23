package com.johan.artisanlink.server.controller.admin;

import com.johan.artisanlink.common.exception.BusinessException;
import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.server.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台统计管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    @Autowired
    private StatisticService statisticService;

    //商户营业统计
    @GetMapping("/merchant")
    public Result merchantStatistics() {
        return null;
    }


}
