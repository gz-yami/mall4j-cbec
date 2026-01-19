/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.event;

import com.yami.shop.bean.app.vo.ProductVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *  获取运费事件
 * @author lhd
 */
@Data
public class CalculateTransfeeEvent {


    @Schema(description ="商品详情信息" )
    ProductVO productVO;


    @Schema(description = "地址id" )
    private Long addrId;

    @Schema(description = "用户ID" )
    private String userId;


    @Schema(description = "物流类型，1 快递 2 自提 3 无需快递 4 同城配送" )
    private Integer deliveryType;

    @Schema(description = "运费")
    private Double transfee;

    public CalculateTransfeeEvent(ProductVO productVO, Long addrId, String userId) {
       this.productVO = productVO;
       this.addrId = addrId;
       this.userId = userId;
    }

}
