/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.delivery.common.model.TransfeeFree;
import com.yami.shop.delivery.common.model.Transport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Lanhai
 */
public interface TransportMapper extends BaseMapper<Transport> {

    /**
     * 获取运费模板和运费金额和运费城市
     *
     * @param id 商品明细
     * @return 运费模板
     */
    Transport getTransportAndTransfeeAndTranscity(@Param("id") Long id);

    /**
     * 根据id删除运费模板
     *
     * @param ids 运费模板id
     */
    void deleteTransports(@Param("ids") Long[] ids);

    /**
     * 获取指定条件包邮项列表
     *
     * @param transportId 运费模板id
     * @return 指定条件包邮项列表
     */
    List<TransfeeFree> getTransfeeFreeAndTranscityFreeByTransportId(@Param("transportId") Long transportId);

}
