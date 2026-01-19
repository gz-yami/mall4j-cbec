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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Yami
 */
@Data
@Schema(description = "设置用户信息")
public class UserRegisterParam {

    @NotBlank
    @Schema(description = "密码" )
    private String password;

    @Schema(description = "手机号" )
    private String mobile;

    @Schema(description = "邮箱")
    private String mail;

    @Schema(description = "校验登陆注册验证码成功的标识" )
    private String checkRegisterSmsFlag;
}
