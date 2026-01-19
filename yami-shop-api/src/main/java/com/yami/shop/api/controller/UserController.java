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


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.UserCenterInfoDto;
import com.yami.shop.bean.app.dto.UserDto;
import com.yami.shop.bean.app.dto.UserInfoDto;
import com.yami.shop.bean.app.param.UserInfoParam;
import com.yami.shop.bean.event.UpdateDistributionUserEvent;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.model.UserExtension;
import com.yami.shop.bean.param.UserParam;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.HideUtil;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * 用户信息
 *
 * @author LGH
 */
@RestController
@RequestMapping("/p/user")
@Tag(name = "用户接口")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final ApplicationContext applicationContext;

    private final OrderService orderService;

    private final ShopDetailService shopDetailService;


    private SysConfigService sysConfigService;


    @GetMapping("/userInfo")
    @Operation(summary = "查看用户信息" , description = "根据用户ID（userId）获取用户信息")
    public ServerResponseEntity<UserDto> userInfo() {
        String userId = SecurityUtils.getUser().getUserId();
        User user = userService.getUserByUserId(userId);
        if (Objects.isNull(user)) {
            return ServerResponseEntity.success();
        }

        UserDto userDto = BeanUtil.map(user, UserDto.class);
        userDto.setUsername(user.getUserName());
        userDto.setProdRecommendation(user.getProdRecommendation());
        if (userDto.getUserMobile() != null) {
            userDto.setMobile(HideUtil.hidePhone(userDto.getUserMobile()));
        }
        return ServerResponseEntity.success(userDto);
    }

    @PutMapping("/setUserInfo")
    @Operation(summary = "设置用户信息" , description = "设置用户信息")
    public ServerResponseEntity<Void> setUserInfo(@RequestBody UserInfoParam userInfoParam) {
        String userId = SecurityUtils.getUser().getUserId();
        User user = new User();
        user.setUserId(userId);
        user.setPic(StrUtil.isBlank(userInfoParam.getAvatarUrl()) ? user.getPic() : userInfoParam.getAvatarUrl());
        user.setSex(userInfoParam.getSex() == null ? user.getSex() : userInfoParam.getSex());
        user.setUserMobile(userInfoParam.getUserMobile() == null ? user.getUserMobile() : userInfoParam.getUserMobile());
        user.setBirthDate(userInfoParam.getBirthDate() == null ? user.getBirthDate() : userInfoParam.getBirthDate());
        user.setUserMail(StrUtil.isNotBlank(userInfoParam.getUserMail()) ? userInfoParam.getUserMail() : user.getUserMail());
        if (StrUtil.isNotBlank(userInfoParam.getNickName())) {
            user.setNickName(userInfoParam.getNickName());
        }
        userService.updateById(user);
        return ServerResponseEntity.success();
    }

    @PutMapping("/setProdRecommendation")
    @Operation(summary = "设置用户个性化推荐" , description = "设置用户个性化推荐")
    public ServerResponseEntity<Void> setProdRecommendation(@RequestBody UserInfoParam userInfoParam) {
        String userId = SecurityUtils.getUser().getUserId();
        User user = new User();
        user.setUserId(userId);
        user.setProdRecommendation(userInfoParam.getProdRecommendation());
        userService.updateById(user);
        return ServerResponseEntity.success();
    }

    @GetMapping("/centerInfo")
    @Operation(summary = "个人中心信息" , description = "获取用户个人中心信息")
    public ServerResponseEntity<UserCenterInfoDto> centerInfo() {
        String userId = SecurityUtils.getUser().getUserId();
        UserCenterInfoDto userCenterInfoDto = new UserCenterInfoDto();
        userCenterInfoDto.setOrderCountData(orderService.getOrderCount(userId));
        userCenterInfoDto.setShopAuditStatus(1);
        ShopDetail shopDetail = shopDetailService.getShopDetailByUserId(userId);
        userCenterInfoDto.setIsSetPassword(shopDetail != null && StrUtil.isNotBlank(shopDetail.getPassword()));
        userCenterInfoDto.setShopId(shopDetail == null ? null : shopDetail.getShopId());
        userCenterInfoDto.setShopStatus(shopDetail == null ? null : shopDetail.getShopStatus());
        return ServerResponseEntity.success(userCenterInfoDto);
    }


    @GetMapping("/getUserScore")
    @Operation(summary = "获取用户积分" , description = "返回用户的积分信息")
    public ServerResponseEntity<UserParam> getUserScore() {
        String userId = SecurityUtils.getUser().getUserId();
        User user = userService.getById(userId);
        UserParam userParam = BeanUtil.map(user, UserParam.class);
        System.out.println();
        return ServerResponseEntity.success(userParam);
    }

    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息" , description = "返回用户的信息")
    public ServerResponseEntity<User> getUserInfo() {
        String userId = SecurityUtils.getUser().getUserId();
        User userInfo = userService.getUserByUserId(userId);
        return ServerResponseEntity.success(userInfo);
    }

    @Operation(summary = "注销账户" , description = "注销账户")
    @GetMapping("/destroy")
    @Parameter(name = "forceDestroy", description = "强制注销" )
    public ServerResponseEntity<Void> destroy(@RequestParam(value = "forceDestroy", required = false, defaultValue = "false") Boolean forceDestroy) {
        String userId = SecurityUtils.getUser().getUserId();
        // 检查此账户是否有未完成的订单
        OrderCountData orderCount = orderService.getOrderCount(userId);

        if (orderCount.getConsignment() + orderCount.getPayed()  + orderCount.getGrouping() > 0) {
            // 存在未完成订单不能注销
            throw new YamiShopBindException("yami.order.unfinished.cannot.destroy.account");
        }
        userService.destroyUser(userId);
        return ServerResponseEntity.success();
    }
}
