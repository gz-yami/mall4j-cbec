/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @Author lth
 * @Date 2021/8/3 14:33
 */
@Data
public class ShopDetailVO {
    @Schema(description = "店铺id")
    private Long shopId;

    @Schema(description = "店铺类型1自营店 2普通店")
    private Integer type;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "店铺简介")
    private String intro;

    @Schema(description = "接收短信号码")
    private String noticeMobile;

    @Schema(description = "店铺logo(可修改)")
    private String shopLogo;

    @Schema(description = "店铺状态(-1:已删除 0: 停业中 1:营业中 2:平台下线 3:开店申请待审核 4:店铺申请中 5:上线申请待审核)")
    private Integer shopStatus;

    @Schema(description = "店铺评分")
    private Double shopScore;

    @Schema(description = "是否优选好店 1.是 0.不是")
    private Integer isPreferred;

    @Schema(description = "店铺收藏数量")
    private Long collectionNum;

    @Schema(description = "移动端背景图")
    private String mobileBackgroundPic;

    @Schema(description = "pc背景图")
    private String pcBackgroundPic;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系方式")
    private String contactPhone;

    @Schema(description = "商家账号")
    private String merchantAccount;

    @Schema(description = "账号状态， 1:启用 0:禁用 -1:删除")
    private Integer accountStatus;

    @Schema(description = "商家名称")
    private String merchantName;

    @Schema(description = "签约起始时间")
    private Date contractStartTime;

    @Schema(description = "签约终止时间")
    private Date contractEndTime;

}
