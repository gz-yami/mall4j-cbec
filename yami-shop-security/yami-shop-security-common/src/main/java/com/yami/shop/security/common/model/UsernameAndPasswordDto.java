/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author yami
 */
@Data
@Schema(description = "用户名和密码参数")
public class UsernameAndPasswordDto {

    @NotBlank(message="用户名不能为空")
    @Schema(description = "用户名" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "邮箱")
    private String mail;

    @NotBlank(message="密码不能为空")
    @Schema(description = "密码" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message="验证码不能为空")
    @Schema(description = "验证码" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
