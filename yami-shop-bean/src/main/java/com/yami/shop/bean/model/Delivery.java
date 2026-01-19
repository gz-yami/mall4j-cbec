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

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yami
 */
@Data
@TableName("tz_delivery")
public class Delivery implements Serializable {

    @TableId
    @Schema(description = "物流id" )
    private Long dvyId;

    @Schema(description = "物流公司名称" )
    private String dvyName;

    @Schema(description = "物流公司编号（tracking17）" )
    private String dvyNoTrack;

    @Schema(description = "物流公司编号（快递100）" )
    private String dvyNoHd;

    @Schema(description = "国家" )
    private String country;

    @Schema(description = "国家（iso）" )
    private String countryIso;

    @Schema(description = "公司主页" )
    private String companyHomeUrl;

    @Schema(description = "建立时间" )
    private Date recTime;

    @Schema(description = "修改时间" )
    private Date modifyTime;

    @Schema(description = "物流查询接口" )
    private String queryUrl;

    @Schema(description = "状态 1：正常 -1：删除" )
    private Integer status;
}
