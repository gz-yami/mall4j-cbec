/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 用于登陆传递账号密码
 *
 * @author FrozenWatermelon
 * @date 2021/01/19
 */
public class BindSocialDTO {

    @NotBlank
    @Schema(description = "尝试社交登录的时候返回的tempUid" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String tempUid;

    @NotBlank
    @Schema(description = "校验账号 手机号" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String validAccount;

    @Schema(description = "验证码" )
    private String validCode;

    public String getTempUid() {
        return tempUid;
    }

    public void setTempUid(String tempUid) {
        this.tempUid = tempUid;
    }

    public String getValidAccount() {
        return validAccount;
    }

    public void setValidAccount(String validAccount) {
        this.validAccount = validAccount;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    @Override
    public String toString() {
        return "BindSocialDTO{" +
                "tempUid='" + tempUid + '\'' +
                ", validAccount='" + validAccount + '\'' +
                ", validCode='" + validCode + '\'' +
                '}';
    }
}
