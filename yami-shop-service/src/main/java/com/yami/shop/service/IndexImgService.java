/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.IndexImg;

import java.util.List;

/**
 *
 * @author lgh on 2018/11/26.
 */
public interface IndexImgService extends IService<IndexImg> {

    /**
     * 根据店铺id与主页轮播图id列表批量删除主页轮播图
     * @param ids 主页轮播图id列表
     * @param shopId 店铺id
     */
    void deleteIndexImgsByIds(Long[] ids, Long shopId);

    /**
     * 根据店铺id与图片类型获取首页轮播图列表信息
     * @param shopId 店铺id
     * @param imgType 图片类型
     * @return 轮播图列表
     */
    List<IndexImg> listIndexImgsByShopId(Long shopId, Integer imgType);

    /**
     * 清除缓存
     * @param shopId
     */
    void removeIndexImgCacheByShopId(Long shopId);

    /**
     * 更新轮播图数据
     * @param ids
     */
    void updateImgProd(List<Long> ids);
}
