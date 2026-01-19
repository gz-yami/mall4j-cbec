/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Yami
 */
@Data
public class ShopDetailParam {

    @NotBlank(message = "{yami.shop.name.not.blank}")
    @Schema(description = "店铺名称(数字、中文，英文(可混合，不可有特殊字符)，可修改)" )
    @Size(max = 50, message = "{yami.shop.name.len.less}")
    private String shopName;

    @Size(max = 200, message = "{yami.shop.desc.len.less}")
    @Schema(description = "店铺简介(可修改)" )
    private String intro;

    @Schema(description = "店铺绑定手机号" )
    @Size(max = 20, message = "{yami.shop.mobile.len.less}")
    private String mobile;

    @Schema(description = "店铺联系电话" )
    @NotBlank(message = "{yami.shop.phone.not.blank}")
    @Size(max = 20, message = "{yami.shop.phone.len.less}")
    private String tel;

    @Schema(description = "店铺logo" )
    @NotBlank(message = "{yami.shop.logo.not.blank}")
    @Size(max = 200, message = "{yami.shop.logo.len.less}")
    private String shopLogo;

    @Schema(description = "店铺相册" )
    @Size(max = 1000, message = "{yami.shop.photo.album.len.less}")
    private String shopPhotos;

    @Schema(description = "营业执照" )
    @Size(max = 200, message = "{yami.business.license.len.less}")
    private String businessLicense;

    @Schema(description = "身份证正面" )
    @Size(max = 200, message = "{yami.id.front.len.less}")
    private String identityCardFront;

    @Schema(description = "身份证反面" )
    @Size(max = 200, message = "{yami.id.negative.len.less}")
    private String identityCardLater;

    @Schema(description = "移动端背景图" )
    private String mobileBackgroundPic;

    @Schema(description = "pc背景图" )
    private String pcBackgroundPic;

    @Schema(description = "商家名称" )
    private String merchantName;

    @Schema(description = "店铺状态(-1:未开通 0: 停业中 1:营业中 2:平台下线 3:平台下线待审核)，可修改" )
    private Integer shopStatus;
}
