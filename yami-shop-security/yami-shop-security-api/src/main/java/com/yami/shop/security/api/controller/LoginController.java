/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.model.User;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PrincipalUtil;
import com.yami.shop.dao.UserMapper;
import com.yami.shop.security.api.dto.SocialAuthenticationDTO;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.dto.AuthenticationDTO;
import com.yami.shop.security.common.manager.PasswordCheckManager;
import com.yami.shop.security.common.manager.PasswordManager;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.vo.TokenInfoVO;
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
public class LoginController {

    private final TokenStore tokenStore;
    private final UserMapper userMapper;
    private final PasswordCheckManager passwordCheckManager;
    private final PasswordManager passwordManager;


    @PostMapping("/login")
    @Operation(summary = "账号密码(用于h5、pc登录)" , description = "通过账号/手机号/用户名密码登录，还要携带用户的类型，也就是用户所在的系统")
    public ServerResponseEntity<TokenInfoVO> login(
            @Valid @RequestBody AuthenticationDTO authenticationDTO) {
        String mobileOrUserName = authenticationDTO.getUserName();
        User user = getUser(mobileOrUserName);
        String decryptPassword = passwordManager.decryptPassword(authenticationDTO.getPassWord());

        // 半小时内密码输入错误十次，已限制登录30分钟
        passwordCheckManager.checkPassword(SysTypeEnum.ORDINARY,user.getUserMobile(),decryptPassword, user.getLoginPassword());

        return ServerResponseEntity.success(getTokenInfoVO(user));
    }

    @PostMapping("/wx/login")
    @Operation(summary = "账号密码登录(用于微信小程序和公众号)" , description = "微信小程序和公众号通过账号/手机号/用户名密码登录, 二合一登录（包含绑定 + 登录）")
    public ServerResponseEntity<TokenInfoVO> wxLogin(
            @Valid @RequestBody SocialAuthenticationDTO authenticationDTO) {
        if (StrUtil.isBlank(authenticationDTO.getTempUid())) {
            return ServerResponseEntity.showFailMsg("tempUid is blank");
        }
        User user = getUser(authenticationDTO.getUserName());
        String decryptPassword = passwordManager.decryptPassword(authenticationDTO.getPassWord());
        // 半小时内密码输入错误十次，已限制登录30分钟
        passwordCheckManager.checkPassword(SysTypeEnum.ORDINARY,user.getUserMobile(),decryptPassword, user.getLoginPassword());
        return ServerResponseEntity.success(getTokenInfoVO(user));
    }

    private TokenInfoVO getTokenInfoVO(User user) {
        if (user.getStatus() == 0) {
            throw new YamiShopBindException("yami.user.disabled");
        }
        // 登录返回token
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(new UidInfoBO(SysTypeEnum.ORDINARY, user.getUserId()));
        return tokenInfoVO;
    }

    private User getUser(String mobileOrUserName) {
        User user = null;
        // 手机验证码登陆，或传过来的账号很像手机号
        if (PrincipalUtil.isMobile(mobileOrUserName)) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserMobile, mobileOrUserName));
        }
        // 邮箱登录
        if (Objects.isNull(user) && PrincipalUtil.isMail(mobileOrUserName)) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserMail, mobileOrUserName));
        }
        // 如果不是手机验证码登陆， 找不到手机号就找用户名
        if  (PrincipalUtil.isUserName(mobileOrUserName)) {
            user = userMapper.selectOneByUserName(mobileOrUserName);
        }
        // 如果不是手机验证码登陆， 找不到手机号就找用户名
        if  (user == null) {
            user = userMapper.selectOneByUserName(mobileOrUserName);
        }
        if (user == null) {
            throw new YamiShopBindException("yami.user.account.error");
        }
        return user;
    }



}
