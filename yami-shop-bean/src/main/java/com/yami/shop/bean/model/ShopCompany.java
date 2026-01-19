/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @Author lth
 * @Date 2021/7/30 14:07
 */
@Data
@TableName("tz_shop_company")
public class ShopCompany {

    @TableId
    private Long shopCompanyId;

    @Schema(description = "店铺id")
    private Long shopId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @NotNull
    @Schema(description = "统一社会信用代码")
    private String creditCode;

    @Schema(description = "企业名称")
    private String firmName;

    @Schema(description = "住所")
    private String residence;

    @Schema(description = "法定代表人")
    private String representative;

    @Schema(description = "法人身份证号")
    private String legalIds;

    @Schema(description = "法人手机号码")
    private String legalPhone;

    @Schema(description = "注册资本（万元）")
    private Double capital;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "成立日期")
    private Date foundTime;

    @Schema(description = "营业起始日期")
    private Date startTime;

    @Schema(description = "营业终止日期")
    private Date endTime;

    @Schema(description = "经营范围")
    private String businessScope;

    @Schema(description = "营业执照")
    private String businessLicense;

    @Schema(description = "身份证正面")
    private String identityCardFront;

    @Schema(description = "身份证反面")
    private String identityCardLater;

    @Schema(description = "工商信息状态：1：已通过 0待审核 -1未通过")
    private Integer status;
}
