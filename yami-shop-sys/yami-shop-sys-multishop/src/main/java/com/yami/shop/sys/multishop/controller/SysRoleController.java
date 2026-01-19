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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.constants.SysCacheNames;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.sys.common.model.SysRole;
import com.yami.shop.sys.common.service.SysMenuService;
import com.yami.shop.sys.common.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 角色管理
 * @author lgh
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/sys/role")
@AllArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;

    private final TokenStore tokenStore;

    @GetMapping("/page")
    @Operation(summary = "分页查询" , description = "分页查询")
    @Parameter(name = "roleName", description = "角色名称" )
    @PreAuthorize("@pms.hasPermission('sys:role:page')")
    public ServerResponseEntity<IPage<SysRole>> page(String roleName, PageParam<SysRole> page){
        IPage<SysRole> sysRoles = sysRoleService.page(page,new LambdaQueryWrapper<SysRole>()
                .like(StrUtil.isNotBlank(roleName),SysRole::getRoleName,roleName)
                .orderByDesc(SysRole::getCreateTime));
        return ServerResponseEntity.success(sysRoles);
    }

    @GetMapping("/list")
    @Operation(summary = "角色列表" , description = "角色列表")
    @PreAuthorize("@pms.hasPermission('sys:role:list')")
    public ServerResponseEntity<List<SysRole>> list(){
        List<SysRole> list = sysRoleService.list();
        return ServerResponseEntity.success(list);
    }

    @GetMapping("/info/{roleId}")
    @Operation(summary = "角色信息" , description = "角色信息")
    @Parameter(name = "roleId", description = "角色id" )
    @PreAuthorize("@pms.hasPermission('sys:role:info')")
    public ServerResponseEntity<SysRole> info(@PathVariable("roleId") Long roleId){
        SysRole role = sysRoleService.getById(roleId);
        //查询角色对应的菜单
        List<Long> menuList = sysMenuService.listMenuIdByRoleId(roleId);
        role.setMenuIdList(menuList);

        return ServerResponseEntity.success(role);
    }

    @SysLog("保存角色")
    @PostMapping
    @Operation(summary = "保存角色" , description = "保存角色")
    @PreAuthorize("@pms.hasPermission('sys:role:save')")
    public ServerResponseEntity<Void> save(@RequestBody SysRole role){

        if (sysRoleService.count(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName())) > 0) {
            // 系统已存在相同角色名称
            throw new YamiShopBindException("yami.sys.role.identical.name");
        }

        sysRoleService.saveRoleAndRoleMenu(role);
        return ServerResponseEntity.success();
    }

    @SysLog("修改角色")
    @PutMapping
    @Operation(summary = "修改角色" , description = "修改角色")
    @PreAuthorize("@pms.hasPermission('sys:role:update')")
    public ServerResponseEntity<Void> update(@RequestBody SysRole role){

        long size = sysRoleService.count(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName())
                .ne(SysRole::getRoleId, role.getRoleId())
        );

        if (size > 0) {
            // 系统已存在相同角色名称
            throw new YamiShopBindException("yami.sys.role.identical.name");
        }
        sysRoleService.updateRoleAndRoleMenu(role);
        // 清除缓存
        List<Long> userIds= sysRoleService.getUserIdsByRoleId(role.getRoleId());
        if (!CollectionUtils.isEmpty(userIds)) {
            List<UidInfoBO> uidInfoList = new ArrayList<>(Constant.INITIAL_CAPACITY);
            List<String> permissionKeys = new ArrayList<>(Constant.INITIAL_CAPACITY);
            for (Long userId : userIds) {
                UidInfoBO uidInfoBO = new UidInfoBO(SysTypeEnum.PLATFORM, userId.toString());
                uidInfoBO.setShopId(Constant.PLATFORM_SHOP_ID);
                uidInfoBO.setAdmin(userId == Constant.SUPER_ADMIN_ID ? 1:0);
                uidInfoList.add(uidInfoBO);
                permissionKeys.add(SysCacheNames.SYS_USER_PERMISSION + CacheNames.UNION + userId);
            }
            //清除拥有该角色的用户token
            tokenStore.deleteAllTokenByUidInfoList(uidInfoList);
            // 清除用户缓存的角色权限
            RedisUtil.del(permissionKeys);
        }
        return ServerResponseEntity.success();
    }

    @SysLog("删除角色")
    @DeleteMapping
    @Operation(summary = "删除角色" , description = "删除角色")
    @Parameter(name = "roleIds", description = "角色id" )
    @PreAuthorize("@pms.hasPermission('sys:role:delete')")
    public ServerResponseEntity<String> delete(@RequestBody Long[] roleIds){
        List<Long> ids = CollUtil.newArrayList(roleIds);
        sysRoleService.deleteBatch(ids);
        return ServerResponseEntity.success();
    }
}
