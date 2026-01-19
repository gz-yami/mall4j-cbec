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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.common.util.AuthUserContext;
import com.yami.shop.sys.common.constant.MenuType;
import com.yami.shop.sys.common.model.SysMenu;
import com.yami.shop.sys.common.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统菜单
 *
 * @author lgh
 */
@Tag(name = "系统菜单")
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Value("${yami.expose.operation.auth:}")
    private Boolean permission;


    @GetMapping("/nav")
    @Operation(summary = "获取用户所拥有的菜单和权限" , description = "通过登陆用户的userId获取用户所拥有的菜单和权限")
    public ServerResponseEntity<Map<Object, Object>> nav() {
        List<SysMenu> menuList = sysMenuService.listMenuByUserId(AuthUserContext.getSysUserId());
        //移除用户隐藏掉的按钮权限
        //获取用户所有权限
        Set<String> authorities = sysMenuService.getUserPermissions(AuthUserContext.getSysUserId());
        authorities.removeAll(
                sysMenuService.listConcealButtonPerms());
        return ServerResponseEntity.success(MapUtil.builder().put("menuList", menuList).put("authorities",  authorities).build());
    }

    @GetMapping("/table")
    @Operation(summary = "获取菜单页面的表" , description = "获取菜单页面的表")
    @PreAuthorize("@pms.hasPermission('sys:menu:table')")
    public ServerResponseEntity<List<SysMenu>> table() {
        List<SysMenu> sysMenuList = sysMenuService.listMenuAndBtn(AuthUserContext.getSysUserId());
        return ServerResponseEntity.success(sysMenuList);
    }

    /**
     * 所有菜单列表(用于新建、修改角色时 获取菜单的信息)
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户所拥有的菜单(不包括按钮)" , description = "通过登陆用户的userId获取用户所拥有的菜单和权限")
    @PreAuthorize("@pms.hasPermission('sys:menu:list')")
    public ServerResponseEntity<List<SysMenu>> list() {
        List<SysMenu> sysMenuList = sysMenuService.listSimpleMenuNoButton();
        return ServerResponseEntity.success(sysMenuList);
    }

    @GetMapping("/listRootMenu")
    @Operation(summary = "选择菜单" , description = "选择菜单")
    @PreAuthorize("@pms.hasPermission('sys:menu:listRootMenu')")
    public ServerResponseEntity<List<SysMenu>> listRootMenu() {
        //查询列表数据
        List<SysMenu> menuList = sysMenuService.listRootMenu();

        return ServerResponseEntity.success(menuList);
    }

    @GetMapping("/listChildrenMenu")
    @Operation(summary = "选择子菜单" , description = "选择子菜单")
    @Parameter(name = "parentId", description = "父菜单id" )
    @PreAuthorize("@pms.hasPermission('sys:menu:listChildrenMenu')")
    public ServerResponseEntity<List<SysMenu>> listChildrenMenu(Long parentId) {
        //查询列表数据
        List<SysMenu> menuList = sysMenuService.listChildrenMenuByParentId(parentId);

        return ServerResponseEntity.success(menuList);
    }

    @GetMapping("/info/{menuId}")
    @Operation(summary = "菜单信息" , description = "菜单信息")
    @Parameter(name = "menuId", description = "菜单id" )
    @PreAuthorize("@pms.hasPermission('sys:menu:info')")
    public ServerResponseEntity<SysMenu> info(@PathVariable("menuId") Long menuId) {
        SysMenu menu = sysMenuService.getSysMenuById(menuId);
        return ServerResponseEntity.success(menu);
    }

    @SysLog("保存菜单")
    @PostMapping
    @Operation(summary = "保存菜单" , description = "保存菜单")
    @PreAuthorize("@pms.hasPermission('sys:menu:save')")
    public ServerResponseEntity<Void> save(@Valid @RequestBody SysMenu menu) {
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }
        sysMenuService.saveSysMenu(menu);
        return ServerResponseEntity.success();
    }

    @SysLog("修改菜单")
    @PutMapping
    @Operation(summary = "修改菜单" , description = "修改菜单")
    @PreAuthorize("@pms.hasPermission('sys:menu:update')")
    public ServerResponseEntity<String> update(@Valid @RequestBody SysMenu menu) {
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }

        if (menu.getType() == MenuType.MENU.getValue()) {
            if (StrUtil.isBlank(menu.getUrl())) {
                // 菜单URL不能为空
                return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.menu.exist"));
            }
        }

        sysMenuService.updateSysMenu(menu);
        // 修改父级菜单是否隐藏，子菜单禁用同步修改
//        //修改子菜单按钮的显示隐藏状态
//        Set<String> perms = sysMenuService.updateSonMenuState(menu.getMenuId(), menu.getHidden());
//        //获取用户所有权限
//        Set<String> authorities = sysMenuService.getUserPermissions(AuthUserContext.getSysUserId());
//        if (menu.getHidden() == 1){
//            authorities.removeAll(perms);
//        }else {
//            authorities.addAll(perms);
//        }

        return ServerResponseEntity.success();
    }

    @SysLog("删除菜单")
    @DeleteMapping("/{menuId}")
    @Operation(summary = "删除" , description = "删除")
    @Parameter(name = "menuId", description = "菜单id" )
    @PreAuthorize("@pms.hasPermission('sys:menu:delete')")
    public ServerResponseEntity<String> delete(@PathVariable Long menuId) {
        if (BooleanUtil.isFalse(permission)) {
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.operate.auth"));
        }
        if (menuId <= Constant.SYS_MENU_MAX_ID) {
            // 系统菜单，不能删除
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.menu.delete.error"));
        }
        //判断是否有子菜单或按钮
        List<SysMenu> menuList = sysMenuService.listChildrenMenuByParentId(menuId);
        if (menuList.size() > 0) {
            // 请先删除子菜单或按钮
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.sys.menu.child.delete"));
        }

        sysMenuService.deleteMenuAndRoleMenu(menuId);

        return ServerResponseEntity.success();
    }


}
