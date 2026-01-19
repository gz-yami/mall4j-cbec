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

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.MyOrderItemDto;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.OrderShopDto;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.param.*;
import com.yami.shop.bean.vo.OrderAmountVO;
import com.yami.shop.bean.vo.OrderAndPayInfoVO;
import com.yami.shop.bean.vo.UnDeliveryOrderExcelVO;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author yami
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号获取订单信息
     *
     * @param orderNumber 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 根据订单状态，获取非退款状态下的订单
     *
     * @param orderStatus        订单状态
     * @param lessThanUpdateTime 最后更新时间
     * @return 非退款状态下的订单
     */
    List<Order> listUnRefundOrderAndOrderItems(@Param("orderStatus") Integer orderStatus, @Param("lessThanUpdateTime") DateTime lessThanUpdateTime);

    /**
     * 获取待发货订单发货超时的列表
     *
     * @param lessPayTime 支付时间
     * @param dateTime
     * @return 非退款状态下的订单
     */
    List<OrderAndPayInfoVO> listDeliveryOutTimeOrder(@Param("lessPayTime") DateTime lessPayTime, @Param("dateTime") DateTime dateTime);

    /**
     * 取消订单
     * @param orders 订单列表
     * @return 取消订单数量
     */
    int cancelOrders(@Param("orders") List<Order> orders);

    /**
     * 确认收货
     *
     * @param orders 订单列表
     */
    int receiptOrder(@Param("orders") List<Order> orders);

    /**
     * 更新成支付成功的状态
     *
     * @param orderNumbers 订单id
     */
    int updateByToPaySuccess(@Param("orderNumbers") List<String> orderNumbers);

    /**
     * 获取订单详情列表
     *
     * @param adapter    分页信息
     * @param orderParam 查询参数
     * @return 订单列表
     */
    List<Order> listOrdersDetailByOrderParam(@Param("adapter") PageAdapter adapter, @Param("orderParam") OrderParam orderParam);

    /**
     * 根据订单查询参数，获取订单数量
     *
     * @param orderParam 查询参数
     * @return 订单数量
     */
    Long countOrderDetail(@Param("orderParam") OrderParam orderParam);

    /**
     * 获取我的订单列表
     *
     * @param adapter 分页参数
     * @param userId  用户id
     * @param status  订单状态
     * @param prodName  订单状态
     * @return 我的订单列表
     */
    List<MyOrderDto> listMyOrderByUserIdAndStatus(@Param("adapter") PageAdapter adapter, @Param("userId") String userId,
                                                  @Param("status") Integer status, @Param("prodName")String prodName);

    /**
     * 获取我的订单列表订单数量
     *
     * @param userId    用户id
     * @param status    订单状态
     * @param isComm    是否评论
     * @param stationId 自提点id
     * @param prodName  商品名称
     * @return 我的订单列表订单数量
     */
    Long countMyOrderByUserIdAndStatus(@Param("userId") String userId, @Param("status") Integer status, @Param("prodName") String prodName, @Param("isComm") Integer isComm,
                                       @Param("stationId") Long stationId);

    /**
     * 获取我的订单列表订单数量
     *
     * @param status    订单状态
     * @param stationId 自提点id
     * @return 我的订单列表订单数量
     */
    Long countByStatusAndStationId(@Param("status") Integer status, @Param("stationId") Long stationId);

    /**
     * 批量删除订单
     *
     * @param orders 订单
     */
    void deleteOrders(@Param("orders") List<Order> orders);

    /**
     * 计算我的订单评价列表数量
     *
     * @param userId 用户id
     * @return 我的订单评价列表数量
     */
    long countMyOrderByUserIdAndComm(@Param("userId") String userId);

    /**
     * 获取各个订单状态的订单数量
     *
     * @param userId 用户id
     * @return 各个订单状态下的订单数量
     */
    OrderCountData getOrderCount(String userId);

    /**
     * 统计订单支付、客单价、订单金额等信息
     *
     * @param shopId    店铺id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 订单支付、客单价、订单金额等信息
     */
    OrderPayParam getOrderCountByShopId(@Param("shopId") Long shopId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 通过24小时分段获取支付金额
     *
     * @param shopId    店铺id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 各个小时的支付金额
     */
    List<OrderPayParam> getActualTotalByHour(@Param("shopId") Long shopId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 通过天数分段获取支付金额
     *
     * @param shopId     店铺id
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 每天支付金额
     */
    List<OrderPayParam> getPayOrderByTime(@Param("shopId") Long shopId, @Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime);

    /**
     * 取消当前已过期的秒杀订单
     *
     * @param orderNumbers 超时取消订单的编号
     */
    boolean batchCancelSeckillOrder(@Param("orderNumbers") List<String> orderNumbers);

    /**
     * 更新订单为待成团
     *
     * @param orderNumber 订单号
     * @return 变更数量
     */
    int updateToWaitGroup(@Param("orderNumber") String orderNumber);

    /**
     * 通过用户id和订单编号获取订单
     *
     * @param orderNumber 订单编号
     * @param userId      用户id
     * @return 订单
     */
    Order getOrderByOrderNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") String userId);

    /**
     * 根据订单号获取订单及订单项
     *
     * @param orderNumber 订单编号
     * @return 订单
     */
    Order getOrderAndOrderItemByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 根据订单号获取订单及订单项
     *
     * @param orderNumbers 订单编号列表
     * @return 订单
     */
    List<Order> listOrderAndOrderItemByOrderNumber(@Param("orderNumbers") List<String> orderNumbers);

    /**
     * 根据订单号获取订单及订单项
     *
     * @param orderNumberList 订单编号列表
     * @return 订单列表
     */
    List<Order> getOrderAndOrderItemByOrderNumberList(@Param("orderNumberList") Set<String> orderNumberList);

    /**
     * 判断是否需要购买置指定商品（购买指定商品的数量）
     *
     * @param prodId 商品id
     * @param userId 用户id
     * @return 购买指定商品的数量
     */
    Integer hasBuySuccessProd(@Param("prodId") Long prodId, @Param("userId") String userId);

    /**
     * 模糊查询订单列表中的某一项订单
     *
     * @param adapter         分页信息
     * @param userId          用户id
     * @param status          订单状态
     * @param orderName       订单商品名
     * @param orderTimeStatus 0全部订单 1最近七天 2最近三个月 3三个月之前 订单
     * @param preTime         七天/三十天
     * @param orderType       订单类型
     * @param orderNumber     订单号
     * @param shopId          店铺id
     * @param orderMold       订单类别
     * @return 我的订单列表
     */
    List<MyOrderDto> listMyOrderByParams(@Param("adapter") PageAdapter adapter, @Param("userId") String userId, @Param("status") Integer status,
                                         @Param("orderName") String orderName, @Param("orderTimeStatus") Integer orderTimeStatus,
                                         @Param("preTime") String preTime, @Param("orderType") Integer orderType, @Param("orderNumber") String orderNumber,
                                         @Param("shopId") Long shopId, @Param("orderMold") Integer orderMold);

    /**
     * 统计模糊查询订单列表中的某一项订单
     *
     * @param userId          用户id
     * @param status          订单状态
     * @param orderName       订单商品名
     * @param orderTimeStatus 0全部订单 1最近七天 2最近三个月 3三个月之前 订单
     * @param preTime         七天/三十天
     * @param orderType       订单类型
     * @param orderNumber     订单号
     * @param shopId          店铺id
     * @param orderMold
     * @return 我的订单列表
     */
    Long countMyOrderByParams(@Param("userId") String userId, @Param("status") Integer status, @Param("orderName") String orderName,
                              @Param("orderTimeStatus") Integer orderTimeStatus, @Param("preTime") String preTime,
                              @Param("orderType") Integer orderType, @Param("orderNumber") String orderNumber, @Param("shopId") Long shopId, @Param("orderMold") Integer orderMold);

    /**
     * 获取订单列表
     *
     * @param orderParam 订单参数
     * @return 订单列表
     */
    List<Order> listOrdersDetailByOrderInfo(@Param("orderParam") OrderParam orderParam);

    /**
     * 获取待发货订单列表
     *
     * @param orderParam 订单参数
     * @return 待发货订单列表
     */
    List<UnDeliveryOrderExcelVO> listUnDeliveryOrderExcel(@Param("orderParam") OrderParam orderParam);

    /**
     * 根据订单id列表获取订单列表
     *
     * @param orderNumberList 订单列表
     * @return 订单列表
     */
    List<Order> getOrderPayInfoByOrderNumber(@Param("orderNumberList") List<String> orderNumberList);

    /**
     * 通过订单编号、店铺id获取订单详情
     *
     * @param orderNumber 订单编号
     * @param shopId      店铺id
     * @return 订单详情
     */
    Order getOrderDetailByOrderNumberAndShopId(@Param("orderNumber") String orderNumber, @Param("shopId") Long shopId);

    /**
     * 根据订单评价状态获取订单列表信息
     *
     * @param pageAdapter 分页参数
     * @param userId      用户id
     * @return 待评价订单列表
     */
    List<MyOrderDto> orderCommentByUserIdAndStatus(@Param("adapter") PageAdapter pageAdapter, @Param("userId") String userId);

    /**
     * 获取自提点的订单列表
     *
     * @param pageAdapter
     * @param stationId   自提点id
     * @param userId      用户id
     * @param shopId      店铺id
     * @return 订单列表
     */
    List<MyOrderDto> getOrderListByStationId(@Param("page") PageAdapter pageAdapter, @Param("stationId") Long stationId,
                                             @Param("userId") String userId, @Param("shopId") Long shopId);

    /**
     * 获取自提点的订单数量
     *
     * @param stationId 自提点id
     * @param userId    用户id
     * @param shopId    店铺id
     * @return 订单列表
     */
    int countOrderListByStationId(@Param("stationId") Long stationId, @Param("userId") String userId, @Param("shopId") Long shopId);

    /**
     * 分页获取自提点订单列表
     *
     * @param pageAdapter 分页参数
     * @param status      订单状态
     * @param stationId   自提点id
     * @return 自提点订单列表
     */
    List<MyOrderDto> orderListByStatusAndStationId(@Param("adapter") PageAdapter pageAdapter, @Param("status") Integer status, @Param("stationId") Long stationId);

    /**
     * 获取待自提的订单
     *
     * @param orderNumberList 订单编号列表
     * @param userId          用户id
     * @param stationId       自提点id
     * @return 订单列表
     */
    List<Order> getStationOrderByOrderNumbers(@Param("orderNumberList") List<String> orderNumberList, @Param("userId") String userId, @Param("stationId") Long stationId);

    /**
     * 获取用户评论订单项
     *
     * @param pageAdapter 分页参数
     * @param userId      用户id
     * @param isComm      是否评论
     * @return 评论订单项列表
     */
    List<MyOrderItemDto> orderItemCommentByUserIdAndStatus(@Param("adapter") PageAdapter pageAdapter, @Param("userId") String userId, @Param("isComm") Integer isComm);

    /**
     * 计算用户评论订单项数量
     *
     * @param userId 用户id
     * @param isComm 是否已评论
     * @return 评论订单项数量
     */
    Long countOrderItemComment(@Param("userId") String userId, @Param("isComm") Integer isComm);

    /**
     * 统计时间内， 成功付款订单的人数和（不剔除退款订单，去重userId）
     *
     * @param param 统计参数
     * @return 成功付款订单的人数和
     */
    Integer countPayCustomerNum(@Param("param") CustomerReqParam param);

    /**
     * 统计时间内, 成交会员数
     *
     * @param param 统计参数
     * @return 成交会员数
     */
    Integer countMemberPayNum(@Param("param") CustomerReqParam param);

    /**
     * 新成交客户数：过去n(beforeYear:过去n年的时间点)年没有购买，在筛选时间内首次在店铺付款的客户数量
     *
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 首次在店铺付款的客户数量
     */
    Integer countNewMemberPayNum(@Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 老成交客户数：过去n(beforeYear:过去n年的时间点)年没有购买，在筛选时间内在店铺再次付款的客户数量
     *
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 再次付款的客户数量
     */
    Integer countOldMemberPayNum(@Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 成交客户数
     *
     * @param param 统计参数
     * @return 全部成交客户占比
     */
    Double countMemberPayAmount(@Param("param") CustomerReqParam param);

    /**
     * 新成交客户数：过去n(beforeYear:过去n年的时间点)年没有购买，在筛选时间内首次在店铺付款金额总和
     *
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 成交客户数
     */
    Double countNewMemberPayAmount(@Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 老成交客户数：过去n(beforeYear:过去n年的时间点)年没有购买，在筛选时间内在店铺再次付款金额总和
     *
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 老成交客户数
     */
    Double countOldMemberPayAmount(@Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 购买客户数
     *
     * @param type      0新客户 其他老客户
     * @param f         f
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 购买客户数
     */
    Integer countPayNumRfm(@Param("type") Integer type, @Param("f") Integer f, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 支付金额
     *
     * @param type      0新客户 其他老客户
     * @param f         f
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 支付金额
     */
    Double sumPayAmountRfm(@Param("type") Integer type, @Param("f") Integer f, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    /**
     * 新成交客户，商品支付件数
     *
     * @param type       0新客户 其他老客户
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 商品支付件数
     */
    Integer countNewOrOldPayProdNum(@Param("type") Integer type, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    /**
     * 统计老/新客户订单数量
     *
     * @param type       0新客户 其他老客户
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 老/新客户订单数量
     */
    Integer countNewOrOldPayTimes(@Param("type") Integer type, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * "成交金额" "成交购买客户数" "成交购买数量" "回购次数"
     *
     * @param shopId     店铺id
     * @param type       0新客户 其他老客户
     * @param cycle      回购周期间隔 10天 30天
     * @param userId     用户id
     * @param payTime    支付时间
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 回购数据统计
     */
    CustomerRepurchaseDetailParam getNewRepurchaseCount(@Param("shopId") Long shopId, @Param("type") Integer type, @Param("cycle") Integer cycle, @Param("userId") String userId, @Param("payTime") Date payTime, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 会员支付数 or 订单数
     *
     * @param param      参数
     * @param isOrderNum 是否订单数
     * @return 会员支付数 or 订单数
     */
    Integer countPaidMemberByParam(@Param("param") MemberReqParam param, @Param("isOrderNum") Integer isOrderNum);

    /**
     * 会员支付金额
     *
     * @param param 参数
     * @return 会员支付金额
     */
    Double countMemberPaidAmount(@Param("param") MemberReqParam param);

    /**
     * 会员支付订单数
     *
     * @param param 参数
     * @return 会员支付订单数
     */
    Integer countMemberPayOrder(@Param("param") MemberReqParam param);

    /**
     * 会员客单价
     *
     * @param param 参数
     * @return 会员客单价
     */
    Integer countPayMemberNum(@Param("param") MemberReqParam param);


    /**
     * 支付订单数
     *
     * @param type       0新客户 其他老客户
     * @param beforeYear 过去n年的时间点
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 支付订单数
     */
    Integer countNewOrOldMemberPayOrder(@Param("type") Integer type, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    /**
     * 新成交会员数
     *
     * @param type         0新客户 其他老客户
     * @param beforeYear   过去n年的时间点
     * @param startTime    开始时间
     * @param dayStartTime
     * @param dayEndTime
     * @return 新成交会员数
     */
    Integer countNewMemberEveryDayPayNum(@Param("type") Integer type, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("dayStartTime") Date dayStartTime, @Param("dayEndTime") Date dayEndTime);

    /**
     * 新支付订单数
     *
     * @param type         0新客户 其他老客户
     * @param beforeYear   过去n年的时间点
     * @param startTime    开始时间
     * @param dayStartTime
     * @param dayEndTime
     * @return
     */
    Integer countNewOrOldMemberEveryDayPayPayOrder(@Param("type") Integer type, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("dayStartTime") Date dayStartTime, @Param("dayEndTime") Date dayEndTime);

    /**
     * 新支付金额
     *
     * @param type         0新客户 其他老客户
     * @param beforeYear   过去n年的时间点
     * @param startTime    开始时间
     * @param dayStartTime
     * @param dayEndTime
     * @return 新支付金额
     */
    Double countNewMemberEveryDayPayAmount(@Param("type") Integer type, @Param("beforeYear") Date beforeYear, @Param("startTime") Date startTime, @Param("dayStartTime") Date dayStartTime, @Param("dayEndTime") Date dayEndTime);

    /**
     * RFM表格返回值
     *
     * @param type
     * @param recentTime 当前
     * @param mouth      1个月
     * @param threeMouth 3个月
     * @param halfYear   半年
     * @param year       一年
     * @return rfm
     */
    List<CustomerRfmRespTableParam> getUserOrderCrateTime(@Param("type") Integer type, @Param("recentTime") Date recentTime, @Param("mouth") Date mouth,
                                                          @Param("threeMouth") Date threeMouth, @Param("halfYear") Date halfYear, @Param("year") Date year);

    /**
     * 获取在一定时间内消费一定次数的会员信息
     *
     * @param isPayed      是否已支付
     * @param deleteStatus
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @param status       状态
     * @param minNum       最小数量
     * @param maxNum       最大数量
     * @return 会员id
     */
    List<String> listUserIdByPurchaseNum(@Param("isPayed") Integer isPayed,
                                         @Param("deleteStatus") Integer deleteStatus,
                                         @Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate,
                                         @Param("status") Integer status,
                                         @Param("minNum") Long minNum,
                                         @Param("maxNum") Long maxNum);

    /**
     * 获取在一定时间内消费一定金额的会员信息
     *
     * @param isPayed      是否已支付
     * @param deleteStatus
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @param status       状态
     * @param minAmount    最小支付金额
     * @param maxAmount    最大支付金额
     * @return 会员id
     */
    List<String> listUserIdByAverageActualTotal(@Param("isPayed") Integer isPayed,
                                                @Param("deleteStatus") Integer deleteStatus,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                @Param("status") Integer status,
                                                @Param("minAmount") BigDecimal minAmount,
                                                @Param("maxAmount") BigDecimal maxAmount);

    /**
     * 最近消费时间, 下单支付的时间(积分支付/余额支付), 充值时间， 比较获取最新时间
     *
     * @param userId 会员id
     * @return 最近消费时间
     */
    Date getLastConsumeDateByUserId(@Param("userId") String userId);

    /**
     * 消费金额, 累计消费了多少金额：除了积分支付的订单累计金额
     *
     * @param userId 会员id
     * @param type   = 0 不统计余额支付部分 type = 1 统计
     * @return 消费金额
     */
    Double countConsumeAmount(@Param("userId") String userId, @Param("type") Integer type);

    /**
     * 消费次数, 累计消费的次数（积分下单不包含），下单的次数
     *
     * @param userId 用户id
     * @return 消费次数, 累计消费的次数（积分下单不包含），下单的次数
     */
    Integer countPayNumByUserId(@Param("userId") String userId);

    /**
     * 平均折扣, 消费下单的优惠总金额/消费次数
     * 优惠总金额
     *
     * @param userId 用户id
     * @return 平均折扣
     */
    Double countReduceAmountByUserId(String userId);

    /**
     * 分页获取订单信息
     *
     * @param page   分页参数
     * @param userId 用户id
     * @return 订单信息
     */
    IPage<Order> getPageByUserId(@Param("page") PageParam<Order> page, @Param("userId") String userId);

    /**
     * 订单统计列表
     *
     * @param param 参数
     * @return 订单统计列表
     */
    List<ProdAnalysisDataParam> prodSurveyList(@Param("param") ProdAnalysisSurveyParam param);

    /**
     * 获取订单的创建状态，给秒杀提供查询是否已经创建订单成功的功能
     *
     * @param orderNumber 订单id
     * @return
     */
    Integer countByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 获取用户的成交留存率, 统计用户第一次成交后，之后的1到6个月的留存用户数
     *
     * @param param 条件查询参数
     * @return 成交留存信息列表
     */
    List<CustomerRetainRespParam> getTradeRetained(@Param("param") CustomerRetainedReqParam param);

    /**
     * 根据订单号和商家id获取信息
     *
     * @param orderNumber 订单编号
     * @param shopId      店铺id
     * @return 信息
     */
    UnDeliveryOrderExcelVO getOrderAndOrderItemsByOrderNumberAndShopId(@Param("orderNumber") String orderNumber, @Param("shopId") Long shopId);

    /**
     * 根据店铺id按订单状态分组获取订单数量
     *
     * @param shopId
     * @return
     */
    OrderCountData getOrderCountOfStatusByShopId(@Param("shopId") Long shopId);

    /**
     * 根据时间获取成交数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 数据
     */
    List<CustomerPayParam> countPayCustomerNumByTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 获取 客户洞察-》消费能力分析  的用户信息
     *
     * @param type      1新成交客户 2老成交客户
     * @param startTime
     * @param endTime
     * @return
     */
    CustomerConsumeRespParam getConsumePowerUserInfo(@Param("type") Integer type, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 计算订单实际金额
     *
     * @param orderNumbers
     * @return
     */
    OrderAmountVO getOrdersActualAmount(@Param("orderNumbers") List<String> orderNumbers);

    /**
     * 获取在一定时间内消费大于一定金额的会员信息
     *
     * @param isPayed      是否已支付
     * @param deleteStatus
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @param status       状态
     * @param minAmount    最小支付金额
     * @return 会员id
     */
    List<String> listUserIdByAverageActualTotals(@Param("isPayed") Integer isPayed,
                                                 @Param("deleteStatus") Integer deleteStatus,
                                                 @Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate,
                                                 @Param("status") Integer status,
                                                 @Param("minAmount") BigDecimal minAmount)
    ;

    /**
     * 商品概况分析
     *
     * @param param
     * @return
     */
    ProdAnalysisDataParam getProdSurvey(@Param("param") ProdAnalysisSurveyParam param);

    /**
     * 获取指定的时间内下单或者支付订单的商品id列表
     *
     * @param startTime
     * @return
     */
    List<Long> listProdIdByTime(@Param("startTime") Date startTime);

    /**
     * 获取指定时间范围内支付订单的商品id列表
     *
     * @param startTime
     * @return
     */
    List<Long> listOrderProdIdByCreateTime(@Param("shopId") Long shopId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据用户id和店铺id分页获取订单信息
     * @param page
     * @param userId
     * @param shopId
     * @return
     */
    IPage<Order> getPageByUserIdAndShopId(@Param("page") PageParam<Order> page, @Param("userId") String userId, @Param("shopId") Long shopId);

    /**
     * 获取超时未支付的秒杀订单
     * @param cancelTime
     * @return
     */
    List<Order> listUnPayOrder(@Param("cancelTime") Date cancelTime);


    /**
     * 根据订单号列表获取订单详细信息列表
     *
     * @param orderNumberList
     * @param userId
     * @return
     */
    List<OrderShopDto> orderDetailByOrderNumberList(@Param("orderNumberList") List<String> orderNumberList, @Param("userId") String userId);
}
