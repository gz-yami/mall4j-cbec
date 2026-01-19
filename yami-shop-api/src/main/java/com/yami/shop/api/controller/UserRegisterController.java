/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.app.param.UserRegisterParam;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.UserPwdUpdateParam;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.*;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.manager.PasswordManager;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.security.common.vo.TokenInfoVO;
import com.yami.shop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户信息
 *
 * @author LGH
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户注册相关接口")
@AllArgsConstructor
public class UserRegisterController {

    private final UserService userService;

    private final TokenStore tokenStore;

    private final PasswordEncoder passwordEncoder;

    private final PasswordManager passwordManager;

    public static final String CHECK_REGISTER_SMS_FLAG = "checkRegisterSmsFlag";

    public static final String CHECK_UPDATE_PWD_SMS_FLAG = "updatePwdSmsFlag";


    /**
     * 只有在pc上使用，所以跟绑定微信openid没啥关系
     */
    @Operation(summary = "注册")
    @PostMapping("/register")
    public ServerResponseEntity<TokenInfoVO> register(@Valid @RequestBody UserRegisterParam userRegisterParam) {
        // 邮箱重复验证
        if (!ObjectUtils.isEmpty(userRegisterParam.getMail())) {
            long count = userService.count(new LambdaQueryWrapper<User>()
                    .eq(User::getUserMail, userRegisterParam.getMail()));
            if (count > 0) {
                throw new YamiShopBindException("yami.user.mail.exist");
            }
        }
        // 新建用户
        User user = new User();
        user.setModifyTime(new Date());
        user.setUserRegtime(new Date());
        user.setUserRegip(IpHelper.getIpAddr());
        user.setStatus(1);
        user.setUserMail(userRegisterParam.getMail());
        if (StrUtil.isNotBlank(userRegisterParam.getPassword())) {
            String decryptPassword = passwordManager.decryptPassword(userRegisterParam.getPassword());
            PasswordUtil.check(decryptPassword);
            user.setLoginPassword(passwordEncoder.encode(decryptPassword));
        }
        // 默认'@'前4位
        String nickName = userRegisterParam.getMail().split("@")[0];
        nickName = "u" + (nickName.length() > 4 ? nickName.substring(nickName.length()-4) : nickName);
        user.setNickName(nickName);
        String userId = IdUtil.simpleUUID();
        user.setUserId(userId);
        user.setLevel(1);
        user.setLevelType(0);
        long registerUserNum = userService.count(new LambdaQueryWrapper<User>().eq(User::getUserMail, userRegisterParam.getMail()).between(User::getStatus, StatusEnum.DISABLE.value(), StatusEnum.ENABLE.value()));
        if (registerUserNum > 0) {
            throw new YamiShopBindException("yami.user.phone.exist");
        }
        userService.save(user);
        // 2. 登录
        return ServerResponseEntity.success(tokenStore.storeAndGetVo(new UidInfoBO(SysTypeEnum.ORDINARY, user.getUserId())));
    }


    @PutMapping("/updatePwd")
    @Operation(summary = "修改密码" , description = "修改密码")
    public ServerResponseEntity<Void> updatePwd(@Valid @RequestBody UserPwdUpdateParam userPwdUpdateParam, HttpServletRequest request) {
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(!ObjectUtils.isEmpty(userPwdUpdateParam.getMobile()), User::getUserMobile, userPwdUpdateParam.getMobile()));
        if (user == null) {
            // 无法获取用户信息
            throw new YamiShopBindException("yami.user.no.exist");
        }
        String decryptPassword = passwordManager.decryptPassword(userPwdUpdateParam.getPassword());
        if (StrUtil.isBlank(decryptPassword)) {
            // 新密码不能为空
            throw new YamiShopBindException("yami.user.password.no.exist");
        }
        if (StrUtil.equals(passwordEncoder.encode(decryptPassword), user.getLoginPassword())) {
            // 新密码不能与原密码相同!
            throw new YamiShopBindException("yami.user.password.check");
        }

        SysTypeEnum sysType = null;
        String accessToken = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(accessToken)) {
            UidInfoBO uidInfo = AuthUserContext.getUidInfo(tokenStore.getUidByToken(accessToken));
            sysType = uidInfo.getSysType();
        }
        user.setModifyTime(new Date());
        PasswordUtil.check(decryptPassword);
        user.setLoginPassword(passwordEncoder.encode(decryptPassword));
        userService.updateUsrInfo(user,sysType);

        tokenStore.deleteTokenByUidInfo(new UidInfoBO(SysTypeEnum.ORDINARY, user.getUserId()));
        return ServerResponseEntity.success();
    }
}
