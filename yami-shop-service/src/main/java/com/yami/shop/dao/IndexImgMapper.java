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
import com.yami.shop.bean.model.IndexImg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yami
 */
public interface IndexImgMapper extends BaseMapper<IndexImg> {

    /**
     * 根据店铺id与主页轮播图id列表批量删除主页轮播图
     *
     * @param ids    主页轮播图id列表
     * @param shopId 店铺id
     */
    void deleteIndexImgsByIds(@Param("ids") Long[] ids, @Param("shopId") Long shopId);

    /**
     * 更新轮播图数据
     * @param ids
     */
    void updateImgProd(@Param("ids") List<Long> ids);
}
