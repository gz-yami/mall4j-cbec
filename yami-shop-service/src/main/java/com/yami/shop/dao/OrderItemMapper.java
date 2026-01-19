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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.param.PayTopParam;
import com.yami.shop.bean.param.ProdAnalysisSurveyParam;
import com.yami.shop.bean.param.ProdEffectParam;
import com.yami.shop.bean.param.ProdEffectRespParam;
import com.yami.shop.bean.vo.OrderItemDetailVO;
import com.yami.shop.bean.vo.SkuStockVO;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author yami
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单号获取订单项信息
     *
     * @param orderNumber 订单号
     * @return 订单项信息
     */
    List<OrderItem> listByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 根据订单号获取订单项信息(不包含赠品)
     *
     * @param orderNumber 订单号
     * @return 订单项信息
     */
    List<OrderItem> getUnGiveawayOrderItemsByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 更新订单项的物流状态
     *
     * @param orderItems 订单项
     */
    void updateByDeliveries(@Param("orderItems") List<OrderItem> orderItems);

    /**
     * 根据订单号获取订单项信息
     *
     * @param orderNumber 订单号
     * @return 订单项信息
     */
    List<OrderItem> getListByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 获取支付金额TOP
     *
     * @param page   分页信息
     * @param param  时间
     * @return 支付金额TOP 订单项
     */
    IPage<PayTopParam> getPayAmounts(PageParam<OrderItem> page, @Param("param") ProdAnalysisSurveyParam param);

    /**
     * 统计某个商品的付款人数
     *
     * @param prodId 商品id
     * @param param  查询范围
     * @return 付款人数
     */
    Integer countPayPerson(@Param("prodId") Long prodId, @Param("param") ProdEffectParam param);

    /**
     * 统计某个商品在特定时间内的付款人数
     *
     * @param prodId    商品id
     * @param shopId    店铺id
     * @param startTime 开始时间
     * @param endTime   结束是按
     * @return 付款人数
     */
    Integer getPayPersonNumByParam(@Param("prodId") Long prodId, @Param("shopId") Long shopId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 获取获取商品效果分析数据
     *
     * @param prodIds   商品id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 商品效果分析数据
     */
    List<ProdEffectRespParam> getProdEffectByDateAndProdIds(@Param("prodIds") List<Long> prodIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据订单项id和语言获取订单项
     *
     * @param orderItemId 订单项id
     * @return 订单项
     */
    OrderItem getByOrderItemId(@Param("orderItemId") Long orderItemId);

    /**
     * 根据订单项获取信息
     *
     * @param orderItemId
     * @return
     */
    OrderItem getInfoByOrderItemId(@Param("orderItemId") Long orderItemId);

    /**
     * 根据订单id查询订单项、退款详情
     *
     * @param orderNumber 订单号
     * @param shopId      店铺id
     * @return 订单项、退款详情
     */
    List<OrderItemDetailVO> listDetailByOrderNumber(@Param("orderNumber") String orderNumber, @Param("shopId") Long shopId);

    /**
     * 获取订单项的商品及商品赠品总数量
     *
     * @param orderItemId
     * @return
     */
    Integer getOrderItemProdNum(@Param("orderItemId") Long orderItemId);

    /**
     * 根据订单编号获取订单项列表及支付时间
     *
     * @param orderNumber
     * @return
     */
    List<OrderItem> listAndPayTimeByOrderNumber(@Param("orderNumber") String orderNumber);


    /**
     * 获取时间范围内的销量排序的商品ids
     * @param shopId 店铺id
     * @param time 时间范围内
     * @param esRenovationSpuSort 商品排序
     * @param prodType 商品类型
     * @param primaryCategoryId 第一级分类id
     * @return 商品ids
     */
    List<Long> getSoldNumRankByShopIdAndTime(@Param("shopId") Long shopId,
                                             @Param("time") Date time,
                                             @Param("esRenovationSpuSort") Integer esRenovationSpuSort,
                                             @Param("prodType") Integer prodType,
                                             @Param("primaryCategoryId") Long primaryCategoryId);

    /**
     * 获取未评论的订单项集合
     * @param endTime 结束时间
     * @return 未评论的订单项集合
     */
    List<OrderItem> listUnCommOrderItem(@Param("endTime") Date endTime);

    /**
     * 统计sku的销量和锁定库存数量
     *
     * @param skuIds
     * @return
     */
    List<SkuStockVO> countSkuSaleAndLockNum(@Param("skuIds") List<Long> skuIds);
}
