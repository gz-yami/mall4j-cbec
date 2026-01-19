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
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.vo.ShopDetailVO;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author LGH
 */
public interface ShopDetailMapper extends BaseMapper<ShopDetail> {

    /**
     * 根据店铺的手机号（登陆账号），获取店铺信息
     *
     * @param mobile 手机号
     * @return 店铺信息
     */
    ShopDetail getShopByMobile(@Param("mobile") String mobile);

    /**
     * 修改用户密码
     *
     * @param username    用户名称/账号
     * @param newPassword 新密码
     */
    void updatePasswordByUserName(@Param("username") String username, @Param("newPassword") String newPassword);

    /**
     * 获取用户店铺信息
     *
     * @param userId 用户id
     * @return 店铺信息
     */
    ShopDetail getShopDetailByUserId(@Param("userId") String userId);

    /**
     * 修改店铺状态
     *
     * @param shopId 店铺id
     * @param status 状态
     */
    void updateStatus(@Param("shopId") Long shopId, @Param("status") Integer status);


    /**
     * 通过经纬度获取距离
     *
     * @param lat    纬度
     * @param lng    经度
     * @param shopId 店铺id
     * @return 距离
     */
    double getDistance(@Param("lat") Double lat, @Param("lng") Double lng, @Param("shopId") Long shopId);


    /**
     * 获取店铺详情
     *
     * @param shopId 店铺id
     * @return 店铺信息
     */
    ShopDetail selectShopDetailById(@Param("shopId") Long shopId);

    /**
     * 批量修改店铺类型
     *
     * @param shopIds 店铺id
     * @param type    店铺类型
     * @return 修改行数
     */
    int batchUpdateShopType(@Param("shopIds") List<Long> shopIds, @Param("type") Integer type);

    /**
     * 根据当前时间获取状态需要改变为停业状态的店铺id列表
     *
     * @param now
     * @return
     */
    List<Long> listShopIdsOfStatusChangeToStopByContractTime(@Param("now") Date now);

    /**
     * 根据当前时间获取状态需要改变为营业状态的店铺id列表
     *
     * @param now
     * @return
     */
    List<Long> listShopIdsOfStatusChangeToOpenByContractTime(@Param("now") Date now);

    /**
     * 批量改变店铺状态
     *
     * @param shopIds
     * @param oldStatus
     * @param newStatus
     */
    void batchUpdateShopStatusByShopIds(@Param("shopIds") List<Long> shopIds, @Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus);

    /**
     * 根据店铺ids获取店铺详情列表
     * @param shopIds
     * @return
     */
    List<ShopDetail> getShopDetailByShopIds(@Param("shopIds") List<Long> shopIds);

    /**
     * 批量修改店铺评分
     * @param shopScoreList
     */
    void batchUpdateShopScore(@Param("shopScoreList") List<ShopDetailVO> shopScoreList);

    /**
     * 保存编号
     * @param shopId
     * @param signAcctType
     * @param acctProtocolNo
     */
    void updateAcctProtocolNoByType(@Param("shopId") Long shopId, @Param("type") Integer signAcctType, @Param("acctProtocolNo") String acctProtocolNo);
}
