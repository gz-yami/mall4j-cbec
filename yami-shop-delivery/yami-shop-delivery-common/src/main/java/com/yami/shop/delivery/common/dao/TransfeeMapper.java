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
import com.yami.shop.delivery.common.model.Transfee;
import com.yami.shop.delivery.common.model.TransfeeFree;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Lanhai
 */
public interface TransfeeMapper extends BaseMapper<Transfee> {

    /**
     * 新增运费项包含城市
     *
     * @param transfees 运费项包含城市
     */
    void insertTransfees(List<Transfee> transfees);

    /**
     * 新增包邮条件项城市
     *
     * @param transfeeFrees 包邮条件项城市
     */
    void insertTransfeeFrees(List<TransfeeFree> transfeeFrees);

    /**
     * 删除运费项包含城市
     *
     * @param transportId 运费项包含城市
     */
    void deleteByTransportId(@Param("transportId") Long transportId);

    /**
     * 删除包邮条件项城市
     *
     * @param transportId 包邮条件项城市
     */
    void deleteTransfeeFreesByTransportId(@Param("transportId") Long transportId);


}
