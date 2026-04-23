package com.johan.artisanlink.server.controller.admin;

import com.johan.artisanlink.common.result.Result;
import com.johan.artisanlink.pojo.vo.BusinessDataVO;
import com.johan.artisanlink.server.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
    public Result<BusinessDataVO> merchantStatistics(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer type) {
        BusinessDataVO data = statisticService.merchantStatistics(merchantId, startTime, endTime);
        return Result.success(data);
    }


}
