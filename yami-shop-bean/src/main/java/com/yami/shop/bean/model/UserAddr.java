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
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户地址管理
 *
 * @author hzm
 * @date 2019-04-15 10:49:40
 */
@Data
@TableName("tz_user_addr")
@EqualsAndHashCode(callSuper = false)
public class UserAddr implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    @Schema(description = "地址id" )
    private Long addrId;

    @Schema(description = "用户ID" )
    private String userId;

    @Schema(description = "收货人" )
    private String receiver;

    @Schema(description = "省ID" )
    private Long provinceId;

    @Schema(description = "省" )
    private String province;

    @Schema(description = "城市" )
    private String city;

    @Schema(description = "城市ID" )
    private Long cityId;

    @Schema(description = "区" )
    private String area;

    @Schema(description = "区ID" )
    private Long areaId;

    @Schema(description = "邮编" )
    private String postCode;

    @Schema(description = "地址" )
    private String addr;

    @Schema(description = "手机" )
    private String mobile;

    @Schema(description = "状态,1正常，0无效" )
    private Integer status;

    @Schema(description = "是否默认地址 1是" )
    private Integer commonAddr;

    @Schema(description = "建立时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer version;

    @Schema(description = "经度" )
    private double lng;

    @Schema(description = "纬度" )
    private double lat;

    @Schema(description = "更新时间" )
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
