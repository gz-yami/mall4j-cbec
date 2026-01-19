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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.param.*;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author lgh on 2018/08/29.
 */
public interface ShopDetailService extends IService<ShopDetail> {

    /**
     * 修改店铺信息
     * @param shopDetail 店铺信息参数
     */
    void updateShopDetail(ShopDetail shopDetail);

    /**
     * 通过经纬度计算距离
     * @param lat 纬度
     * @param lng 经度
     * @param shopId 店铺id
     * @return 距离
     */
    double getDistance(Double lat, Double lng, Long shopId);

    /**
     * 根据店铺id获取店铺信息
     * @param shopId 店铺id
     * @return 店铺信息
     */
    ShopDetail getShopDetailByShopId(Long shopId);

    /**
     * 删除店铺详情缓存
     * @param shopId 店铺id
     */
    void removeShopDetailCacheByShopId(Long shopId);

    /**
     * 根据店铺的手机号（登陆账号），获取店铺信息
     *
     * @param mobile 手机号
     * @return 店铺详情
     */
    ShopDetail getShopByMobile(@Param("mobile") String mobile);


    /**
     * 根据用户id查询店铺信息
     * @param userId 用户id
     * @return 店铺信息
     */
    ShopDetail getShopDetailByUserId(String userId);

    /**
     * 统计店铺电话号码
     * @param mobile 电话号码
     * @param shopId 店铺id
     * @return 数量
     */
    long checkMobile(String mobile, Long shopId);

    /**
     * 根据店铺ids获取店铺详情列表
     * @param shopIds 店铺ids
     * @return
     */
    List<ShopDetail> getShopDetailByShopIds(List<Long> shopIds);

}
