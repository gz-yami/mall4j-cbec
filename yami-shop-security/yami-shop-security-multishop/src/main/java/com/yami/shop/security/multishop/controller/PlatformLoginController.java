/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.multishop.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.manager.PasswordCheckManager;
import com.yami.shop.security.common.manager.PasswordManager;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.vo.TokenInfoVO;
import com.yami.shop.security.multishop.dto.CaptchaAuthenticationDTO;
import com.yami.shop.sys.common.dao.SysUserMapper;
import com.yami.shop.sys.common.model.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author FrozenWatermelon
 * @date 2020/6/30
 */
@RestController
@Tag(name = "登录")
@AllArgsConstructor
public class PlatformLoginController {

    private final TokenStore tokenStore;
    private final SysUserMapper sysUserMapper;
    private final PasswordCheckManager passwordCheckManager;
    private final CaptchaService captchaService;
    private final PasswordManager passwordManager;

    @PostMapping("/platformLogin")
    @Operation(summary = "账号密码 + 验证码登录(用于后台登录)" , description = "通过账号/手机号/用户名密码登录")
    public ServerResponseEntity<?> login(
            @Valid @RequestBody CaptchaAuthenticationDTO captchaAuthenticationDTO) {
        // 登陆后台登录需要再校验一遍验证码
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(captchaAuthenticationDTO.getCaptchaVerification());
        ResponseModel response = captchaService.verification(captchaVO);
        if (!response.isSuccess()) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.user.code.error"));
        }

        SysUser sysUser = sysUserMapper.selectByUsername(captchaAuthenticationDTO.getUserName());
        if (sysUser == null) {
            throw new YamiShopBindException("yami.user.account.error");
        }

        String decryptPassword = passwordManager.decryptPassword(captchaAuthenticationDTO.getPassWord());

        // 半小时内密码输入错误十次，已限制登录30分钟
        passwordCheckManager.checkPassword(SysTypeEnum.MULTISHOP,captchaAuthenticationDTO.getUserName(), decryptPassword, sysUser.getPassword());

        // 不是店铺超级管理员，并且是禁用状态，无法登录
        if (sysUser.getUserId()!= Constant.SUPER_ADMIN_ID && Objects.equals(sysUser.getStatus(),0)) {
            // 未找到此用户信息
            throw new YamiShopBindException("yami.platform.user.account.lock");
        }
        UidInfoBO uidInfoBO = new UidInfoBO(SysTypeEnum.PLATFORM, sysUser.getUserId().toString(), Constant.PLATFORM_SHOP_ID);
        uidInfoBO.setAdmin(sysUser.getUserId()== Constant.SUPER_ADMIN_ID ? 1:0);
        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(uidInfoBO);
        return ServerResponseEntity.success(tokenInfoVO);
    }






}
