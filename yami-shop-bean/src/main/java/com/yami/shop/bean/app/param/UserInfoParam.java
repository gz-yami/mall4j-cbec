/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yami
 */
@Data
@Schema(description = "设置用户信息")
public class UserInfoParam {

    @Schema(description = "用户昵称" )
    private String nickName;

    @Schema(description = "用户头像" )
    private String avatarUrl;

    @Schema(description = "用户性别" )
    private String sex;

    @Schema(description = "邮箱" )
    private String userMail;

    @Schema(description = "用户生日" )
    private String birthDate;

    @Schema(description = "用户手机号" )
    private String userMobile;

    @Schema(description = "商品个性化推荐 1:开启 0:关闭" )
    private Integer prodRecommendation;


}
