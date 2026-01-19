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

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("tz_user_addr_order")
public class UserAddrOrder implements Serializable {

    @TableId
    @Schema(description = "ID" )
    private Long addrOrderId;

    @Schema(description = "地址ID" )
    private Long addrId;

    @Schema(description = "用户ID" )
    private String userId;

    @Schema(description = "收货人" )
    private String receiver;

    @Schema(description = "省" )
    private String province;

    @Schema(description = "城市" )
    private String city;

    @Schema(description = "区" )
    private String area;

    @Schema(description = "地址" )
    private String addr;

    @Schema(description = "邮编" )
    private String postCode;

    @Schema(description = "省ID" )
    private Long provinceId;

    @Schema(description = "城市ID" )
    private Long cityId;

    @Schema(description = "区域ID" )
    private Long areaId;

    @Schema(description = "手机" )
    private String mobile;

    @Schema(description = "建立时间" )
    private Date createTime;

    @Schema(description = "版本号" )
    private Integer version;

    @Schema(description = "经度" )
    @TableField(exist = false)
    private double lng;

    @Schema(description = "纬度" )
    @TableField(exist = false)
    private double lat;

}
