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
 * 自提点
 *
 * @author LGH
 * @date 2020-04-23 15:18:29
 */
@Data
@TableName("tz_station")
public class Station implements Serializable{
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "自提点id" )
    private Long stationId;

    @Schema(description = "关联店铺" )
    private Long shopId;

    @Schema(description = "自提点名称" )
    private String stationName;

    @Schema(description = "自提点图片" )
    private String pic;

    @Schema(description = "电话区号" )
    private String phonePrefix;

    @Schema(description = "手机/电话号码" )
    private String phone;

    @Schema(description = "自提点状态 -1:已删除 0:关闭 1:营业 2:强制关闭 3:审核中 4:审核失败" )
    private Integer status;

    @Schema(description = "创建时间" )
    private Date createTime;

    @Schema(description = "更新时间" )
    private Date updateTime;

    @Schema(description = "省id" )
    private Long provinceId;

    @Schema(description = "省" )
    private String province;

    @Schema(description = "市id" )
    private Long cityId;

    @Schema(description = "市" )
    private String city;

    @Schema(description = "区id" )
    private Long areaId;

    @Schema(description = "区" )
    private String area;

    @Schema(description = "地址" )
    private String addr;

    @Schema(description = "经度" )
    private Double lng;

    @Schema(description = "纬度" )
    private Double lat;

    @Schema(description = "账号" )
    private String account;

    @Schema(description = "密码" )
    private String password;

    @Schema(description = "时间日期数据" )
    private String timeDate;

    @Schema(description = "库存模式 1共享总部库存 2独立销售库存" )
    private Integer stockMode;

    @TableField(exist = false)
    @Schema(description = "店铺名称" )
    private String shopName;

    @TableField(exist = false)
    @Schema(description = "skuId" )
    private Long skuId;

    @TableField(exist = false)
    @Schema(description = "库存" )
    private Integer stock;

    @TableField(exist = false)
    @Schema(description = "是否展示所有的门店(包含已删除的,目前秒杀用到) 1.是 0.否" )
    private Integer showAll;

    @Data
    public static class TimeDateModeVO {

        @Schema(description = "营业时间 shopTime[营业开始时间，营业结束时间]" )
        private String[] shopTime;

        @Schema(description = "时段间隔 1:天 2:上下午晚上（12:00和18:00为分界点）  3:小时  4:半小时" )
        private Integer interval;

        @Schema(description = "自提开始时间" )
        private String stationStartTime;

        @Schema(description = "自提结束时间" )
        private String stationEndTime;
    }

}
