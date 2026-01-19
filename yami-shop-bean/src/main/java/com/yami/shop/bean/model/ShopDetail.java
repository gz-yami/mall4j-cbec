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
@TableName("tz_shop_detail")
public class ShopDetail implements Serializable {
    private static final long serialVersionUID = 3300529542917772262L;

    @TableId
    @Schema(description = "店铺id")
    private Long shopId;

    @Schema(description = "店铺名称")
    private String shopName;

    @Schema(description = "店长用户id")
    private String userId;

    @TableField(exist = false)
    @Schema(description = "店长用户名称")
    private String userName;

    @Schema(description = "店铺简介")
    private String intro;

    @Schema(description = "店长")
    private String shopOwner;

    @Schema(description = "店铺绑定的手机")
    private String mobile;

    @Schema(description = "店铺邮箱")
    private String mail;

    @Schema(description = "登录密码")
    private String password;

    @Schema(description = "店铺联系电话")
    private String tel;

    @Schema(description = "店铺logo")
    private String shopLogo;

    @Schema(description = "店铺状态(-1:未开通 0: 停业中 1:营业中 2:平台下线 3:平台下线待审核)")
    private Integer shopStatus;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "分销设置(0关闭 1开启)")
    private Integer isDistribution;

    @Schema(description = "营业执照")
    private String businessLicense;

    @Schema(description = "身份证正面")
    private String identityCardFront;

    @Schema(description = "身份证反面")
    private String identityCardLater;

    @Schema(description = "0普通店铺 1优选好店")
    private Integer type;

    @Schema(description = "签约起始时间")
    private Date contractStartTime;

    @Schema(description = "签约终止时间")
    private Date contractEndTime;

    @Schema(description = "店铺评分")
    private Double shopScore;

    @Schema(description = "移动端背景图")
    private String mobileBackgroundPic;

    @Schema(description = "pc背景图")
    private String pcBackgroundPic;

    @Schema(description = "商家名称")
    private String merchantName;

    @Schema(description = "店铺销量")
    private Long shopSoldNum;

    @Schema(description = "是否为平台创建 1是0否")
    private Integer isPlatform;

    @Schema(description = "商家账号")
    @TableField(exist = false)
    private String merchantAccount;

    @Schema(description = "账号状态， 1:启用 0:禁用 -1:删除")
    @TableField(exist = false)
    private Integer accountStatus;

    @TableField(exist = false)
    private Long employeeId;

    @Schema(description = "是否开店 1是0否")
    @TableField(exist = false)
    private Integer isPassShop;

    @Schema(description = "店铺管理员手机")
    @TableField(exist = false)
    private String adminMobile;
}
