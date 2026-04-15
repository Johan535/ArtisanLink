package com.johan.artisanlink.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johan.artisanlink.pojo.po.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户Mapper
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
}
