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
import com.yami.shop.bean.app.dto.MyOrderItemDto;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.param.PayTopParam;
import com.yami.shop.bean.param.ProdAnalysisSurveyParam;
import com.yami.shop.bean.vo.OrderDetailVO;
import com.yami.shop.common.util.PageParam;

import java.util.Date;
import java.util.List;

/**
 * @author lgh on 2018/09/15.
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单号获取订单项信息
     * @param orderNumber 订单号
     * @param handleActivity 是否需要处理活动商品
     * @return 订单项信息
     */
    List<OrderItem> getOrderItemsByOrderNumber(String orderNumber);

    /**
     * 根据订单号获取订单项信息(不包含赠品)
     * @param orderNumber 订单号
     * @return 订单项信息
     */
    List<OrderItem> getUnGiveawayOrderItemsByOrderNumber(String orderNumber);
    /**
     * 获取支付金额TOP
     * @param page 分页信息
     * @param param 时间
     * @return 支付金额TOP 订单项
     */
    IPage<PayTopParam> getPayAmounts(PageParam<OrderItem> page, ProdAnalysisSurveyParam param);

    /**
     * 统计某个商品在特定时间内的付款人数
     * @param prodId 商品id
     * @param shopId 店铺id
     * @param startTime 开始时间
     * @param endTime 结束是按
     * @return 付款人数
     */
    Integer getPayPersonNumByParam(Long prodId,Long shopId ,Date startTime,Date endTime);

    /**
     * 批量保存订单项信息
     * @param orderItems 订单项
     */
    void insertBatchOrderItem(List<OrderItem> orderItems);

    /**
     * 根据订单id和语言获取订单项
     * @param orderItemId 订单项id
     * @return 订单项
     */
    OrderItem getByOrderItemId(Long orderItemId);

    /**
     * 根据订单项id获取信息
     * @param orderItemId
     * @return
     */
    OrderItem getOrderItemAndRefundByOrderItemId(Long orderItemId);

    /**
     * 根据订单id查询订单项、退款详情
     *
     * @param orderNumber 订单id
     * @param refundSn    退款单号
     * @param reason
     * @param shopId 店铺id
     * @return 订单项、退款详情
     */
    OrderDetailVO listDetailByParam(String orderNumber, String refundSn, Integer reason, Long shopId);

    /**
     * 获取订单项的商品及商品赠品总数量
     * @param orderItemId
     * @return
     */
    Integer getOrderItemProdNum(Long orderItemId);


    /**
     * 获取评论详情
     * @param orderItemId
     * @return
     */
    OrderItem getByIdI18n(Long orderItemId);

    /**
     * 根据订单编号获取订单项列表及支付时间
     * @param orderNumber
     * @return
     */
    List<OrderItem> listAndPayTimeByOrderNumber(String orderNumber);


    /**
     * 获取时间范围内的销量排序的商品ids
     * @param key redisKey名称
     * @param shopId 店铺id
     * @param dayNum 时间范围内
     * @param expireTime 过期时间
     * @param esRenovationSpuSort 排序方式
     * @param primaryCategoryId 第一分类id
     * @return 商品ids
     */
    List<Long> getSoldNumRankByShopIdAndTime(String key,
                                             Long shopId,
                                             Integer dayNum,
                                             Integer expireTime,
                                             Integer esRenovationSpuSort,
                                             Long primaryCategoryId);

    /**
     * 处理一下订单项活动信息
     * @param orderItems
     * @param myOrderItemDtos
     * @return
     */
    public void handleActivityOrderItem(List<OrderItem> orderItems,List<MyOrderItemDto> myOrderItemDtos);

    /**
     * 获取未评论的订单项集合
     * @param endTime 结束时间
     * @return 未评论的订单项集合
     */
    List<OrderItem> listUnCommOrderItem(Date endTime);

//    List<OrderItem> getUnCloseRefundOrderItemByOrderNumber(String orderNumber);
}
