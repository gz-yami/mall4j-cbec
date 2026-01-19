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
import com.yami.shop.delivery.common.model.Transcity;
import com.yami.shop.delivery.common.model.TranscityFree;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Lanhai
 */
public interface TranscityMapper extends BaseMapper<Transcity> {

    /**
     * 新增运费项包含城市
     *
     * @param transcities 运费项包含城市
     */
    void insertTranscities(@Param("transcities") List<Transcity> transcities);

    /**
     * 新增包邮条件项城市
     *
     * @param transcityFrees 包邮条件项城市
     */
    void insertTranscityFrees(@Param("transcityFrees") List<TranscityFree> transcityFrees);

    /**
     * 批量删除运费项包含城市
     *
     * @param transfeeIds 运费项包含城市
     */
    void deleteBatchByTransfeeIds(@Param("transfeeIds") List<Long> transfeeIds);

    /**
     * 批量删除包邮条件项城市
     *
     * @param transfeeFreeIds 包邮条件项城市
     */
    void deleteBatchByTransfeeFreeIds(@Param("transfeeFreeIds") List<Long> transfeeFreeIds);

}
