/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author LGH
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderItemDto extends ProductItemDto implements Serializable {

    @Schema(description = "订单项id" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderItemId;

    @Schema(description = "订单项赠品列表" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDto> giveawayList;

    @Schema(description = "退款订单编号，如果为null时，说明为正常订单" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String refundSn;

    @Schema(description = "处理退款状态:(1.买家申请 2.卖家接受 3.买家发货 4.卖家收货 5.退款成功 6.买家撤回申请 7.商家拒绝 -1.退款关闭)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer returnMoneySts;


    @Schema(description = "赠送主订单项id" )
    private Long giveawayOrderItemId;

    @Schema(description = "订单编号")
    private String orderNumber;
    @Schema(description = "活动id")
    private Long activityId;

    @Schema(description = "活动类型 5.赠品 6.组合")
    private Integer activityType;

    @Schema(description = "订单项子组合商品列表" , requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(exist = false)
    private List<OrderItemDto> comboList;


}
