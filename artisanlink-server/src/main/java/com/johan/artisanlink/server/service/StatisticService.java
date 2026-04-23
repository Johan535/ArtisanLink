package com.johan.artisanlink.server.service;

import com.johan.artisanlink.pojo.vo.BusinessDataVO;

import java.time.LocalDateTime;

public interface StatisticService {
    BusinessDataVO merchantStatistics(Long merchantId, LocalDateTime startTime, LocalDateTime endTime);
}
