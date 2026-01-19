/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.model.ProdExtension;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 *
 * @author LGH
 * @date 2022-05-05 10:47:48
 */
public interface ProdExtensionMapper extends BaseMapper<ProdExtension> {

    /**
     * 更新注水销量
     *
     * @param waterSaleNum
     * @param prodId
     * @return
     */
    int updateWaterSoldNum(@Param("waterSoldNum") Integer waterSaleNum, @Param("prodId") Long prodId);

    /**
     * 根据商品Id计算总销量
     * @param prodIdList 商品Id列表
     * @return
     */
    Long getSoldNumByProdIds(@Param("prodIdList") List<Long> prodIdList);
}
