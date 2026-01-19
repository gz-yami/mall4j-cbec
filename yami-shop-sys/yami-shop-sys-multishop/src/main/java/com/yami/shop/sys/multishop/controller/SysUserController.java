/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.multishop.controller;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.common.util.PasswordUtil;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.manager.PasswordManager;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.model.UpdatePasswordDto;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.sys.common.model.SysUser;
import com.yami.shop.sys.common.service.SysRoleService;
import com.yami.shop.sys.common.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 系统用户
 * @author lgh
 */
@Tag(name = "系统用户")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    @Value("${yami.expose.operation.auth:}")
    private Boolean permission;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;
    private final PasswordManager passwordManager;


    @GetMapping("/page")
    @Operation(summary = "分页查询" , description = "分页查询")
    @Parameters(value = {
            @Parameter(name = "username", description = "用户名" ),
            @Parameter(name = "nickName", description = "用户昵称" )
    })
    @PreAuthorize("@pms.hasPermission('sys:user:page')")
    public ServerResponseEntity<IPage<SysUser>> page(String username, String nickName, PageParam<SysUser> page){

        IPage<SysUser> sysUserPage = sysUserService.page(page, new LambdaQueryWrapper<SysUser>()
                .like(StrUtil.isNotBlank(username), SysUser::getUsername, username)
                .like(StrUtil.isNotBlank(nickName), SysUser::getNickName, nickName));
        for (SysUser record : sysUserPage.getRecords()) {
            record.setMobile(PhoneUtil.hideBetween(record.getMobile()).toString());
        }

        return ServerResponseEntity.success(sysUserPage);
    }

    @Operation(summary = "获取系统用户集合")
    @GetMapping("/list")
    public ServerResponseEntity<List<SysUser>> list() {
        List<SysUser> sysUsers = sysUserService.listSysUser();
        return ServerResponseEntity.success(sysUsers);
    }

    @GetMapping("/info")
    @Operation(summary = "获取登录的用户信息" , description = "获取登录的用户信息")
    public ServerResponseEntity<SysUser> info(){
        return ServerResponseEntity.success(sysUserService.getSysUserById(AuthUserContext.getSysUserId()));
    }

    @SysLog("修改密码")
    @PostMapping("/password")
    @Operation(summary = "修改密码" , description = "修改当前登陆用户的密码")
    public ServerResponseEntity<String> password(@RequestBody @Valid UpdatePasswordDto param) {
        Long userId = AuthUserContext.getSysUserId();
        if (Objects.equals(userId.intValue(), Constant.SUPER_ADMIN_ID) && BooleanUtil.isFalse(permission)) {
            throw new YamiShopBindException("yami.no.auth");
        }
        SysUser dbUser = sysUserService.getSysUserById(userId);
        String password = passwordManager.decryptPassword(param.getPassword());
        if (!passwordEncoder.matches(password, dbUser.getPassword())) {
            // 原密码不正确
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.password.error"));
        }
        //新密码
        String decryptPassword = passwordManager.decryptPassword(param.getNewPassword());
        PasswordUtil.check(decryptPassword);
        String newPassword = passwordEncoder.encode(decryptPassword);
//        更新密码
        sysUserService.updatePasswordByUserId(userId, newPassword);
        tokenStore.deleteTokenByUidInfo(AuthUserContext.get());
        return ServerResponseEntity.success();
    }

    @GetMapping("/info/{userId}")
    @Operation(summary = "用户信息" , description = "用户信息")
    @Parameter(name = "userId", description = "用户id" )
    @PreAuthorize("@pms.hasPermission('sys:user:info')")
    public ServerResponseEntity<SysUser> info(@PathVariable("userId") Long userId){
        SysUser user = sysUserService.getSysUserById(userId);
        user.setMobile(PhoneUtil.hideBetween(user.getMobile()).toString());
        if (Objects.equals(userId, AuthUserContext.getSysUserId())) {
            user.setIsSelf(1);
        }
        user.setUserId(null);
        //获取用户所属的角色列表
        List<Long> roleIdList = sysRoleService.listRoleIdByUserId(userId);
        user.setRoleIdList(roleIdList);
        return ServerResponseEntity.success(user);
    }

    @GetMapping("/sysUserInfo")
    @Operation(summary = "获取用户信息" , description = "获取用户信息")
    @PreAuthorize("@pms.hasPermission('sys:user:info')")
    public ServerResponseEntity<SysUser> sysUserInfo(){
        SysUser user = sysUserService.getSysUserById(AuthUserContext.getSysUserId());
        user.setUserId(null);
        user.setPassword(null);
        return ServerResponseEntity.success(user);
    }

    @SysLog("保存用户")
    @PostMapping
    @Operation(summary = "保存用户" , description = "保存用户")
    @PreAuthorize("@pms.hasPermission('sys:user:save')")
    public ServerResponseEntity<String> save(@Valid @RequestBody SysUser user){
        String username = user.getUsername();
        SysUser dbUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (dbUser!=null) {
            // 该用户已存在
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.user.already"));
        }
        String decryptPassword = passwordManager.decryptPassword(user.getPassword());
        PasswordUtil.check(decryptPassword);
        user.setPassword(passwordEncoder.encode(decryptPassword));
        sysUserService.saveUserAndUserRole(user);

        return ServerResponseEntity.success();
    }

    @SysLog("修改用户")
    @PutMapping
    @Operation(summary = "修改用户" , description = "修改用户")
    @PreAuthorize("@pms.hasPermission('sys:user:update')")
    public ServerResponseEntity<String> update(@Valid @RequestBody SysUser user){
        if (Objects.equals(user.getUserId().intValue(), Constant.SUPER_ADMIN_ID) && BooleanUtil.isFalse(permission)) {
            throw new YamiShopBindException("yami.no.auth");
        }
        String password = passwordManager.decryptPassword(user.getPassword());
        SysUser dbUserNameInfo = sysUserService.getByUserName(user.getUsername());

        //修改管理员账号但修改人不是管理员，则抛出异常（只有管理员可以改管理员信息）
        if (user.getUserId() == Constant.SUPER_ADMIN_ID &&
                AuthUserContext.getSysUserId() != Constant.SUPER_ADMIN_ID) {
            // 您没有权限修改管理员信息
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.not.permission.modify.administrator.info"));
        }
        if (dbUserNameInfo != null && !Objects.equals(dbUserNameInfo.getUserId(),user.getUserId())) {
            // 该用户已存在
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.user.already"));
        }
        if (StrUtil.isBlank(password)) {
            user.setPassword(null);
        }else {
            PasswordUtil.check(password);
            user.setPassword(passwordEncoder.encode(password));
        }
        sysUserService.updateUserAndUserRole(user);
        if (Objects.equals(dbUserNameInfo.getStatus(), 1) && Objects.equals(user.getStatus(), 0)) {
            // 禁用用户
            SysUser admin = sysUserService.getSysUserById(Constant.SUPER_ADMIN_ID);
        }
        tokenStore.deleteTokenByUidInfo(new UidInfoBO(SysTypeEnum.PLATFORM, user.getUserId().toString(),
                Constant.PLATFORM_SHOP_ID, dbUserNameInfo.getUserId() == 1 ? 1 : 0));
        return ServerResponseEntity.success();
    }

    @SysLog("删除用户")
    @DeleteMapping
    @Operation(summary = "删除用户" , description = "删除用户")
    @Parameter(name = "userIds", description = "用户id列表" )
    @PreAuthorize("@pms.hasPermission('sys:user:delete')")
    public ServerResponseEntity<String> delete(@RequestBody Long[] userIds){
        if (userIds.length == 0) {
            // 请选择需要删除的用户
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.select.user"));
        }
        if(ArrayUtil.contains(userIds, Constant.SUPER_ADMIN_ID)){
            // 系统管理员不能删除
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.admin.error"));
        }
        if(ArrayUtil.contains(userIds, AuthUserContext.getSysUserId())){
            // 当前用户不能删除
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.delete.error"));
        }
        sysUserService.removeByUserIds(Arrays.asList(userIds));
        SysUser admin = sysUserService.getSysUserById(Constant.SUPER_ADMIN_ID);
        // 删除系统用户后，清除token
        Arrays.asList(userIds).forEach(userId -> {
            tokenStore.deleteTokenByUidInfo(new UidInfoBO(SysTypeEnum.PLATFORM, userId.toString(),
                    Constant.PLATFORM_SHOP_ID, userId == 1 ? 1 : 0));
        });
        return ServerResponseEntity.success();
    }



}
