/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.sys.common.model.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * 菜单管理
 *
 * @author lgh
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色id获取菜单列表
     *
     * @param roleId 角色id
     * @return 菜单id列表
     */
    List<Long> listMenuIdByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询用户的所有菜单ID
     *
     * @param userId 用户id
     * @return 该用户所有可用的菜单
     */
    List<SysMenu> listMenuByUserId(@Param("userId") Long userId);

    /**
     * 获取系统的所有菜单
     *
     * @return 系统的所有菜单
     */
    List<SysMenu> listMenu();

    /**
     * 获取简单的menu tree 用于在 ele-ui tree中显示，根据orderNum排序
     *
     * @return ztreeDto列表
     */
    List<SysMenu> listSimpleMenuNoButton();

    /**
     * 获取一级菜单
     *
     * @return 一级菜单列表
     */
    List<SysMenu> listRootMenu();

    /**
     * 根据一级菜单id 获取二级菜单
     *
     * @param parentId 一级菜单id
     * @return 二级菜单列表
     */
    List<SysMenu> listChildrenMenuByParentId(@Param("parentId") Long parentId);

    /**
     * 批量获取菜单以及按钮
     *
     * @return 系统的所有菜单以及按钮
     */
    List<SysMenu> listMenuAndBtn();

    /**
     * 根据授权信息获取菜单数据
     * @param perms 授权信息
     * @return
     */
    List<SysMenu> listMenuNameByPerms(@Param("perms") String perms);

    /**
     * 根据父菜单id修改子菜单和子按钮的是否隐藏状态
     * @param parentId 父id
     * @param state 状态
     * @return
     */
    int updateSonMenuState(@Param("parentId") Long parentId, @Param("state") Integer state);

    /**
     * 根据父菜单id获取所以子按钮的权限
     * @param parentId
     * @return
     */
    Set<String> listMenuPerms(@Param("parentId") Long parentId);

    /**
     * 获取隐藏的按钮权限
     * @return
     */
    Set<String> listConcealButtonPerms();

    /**
     * 根据角色id批量获取角色拥有菜单以及按钮
     *
     * @param userId 角色id
     * @return 系统的所有菜单以及按钮
     */
    List<SysMenu> listRoleMenuAndBtn(@Param("userId") Long userId);

    /**
     * 获取指定系统菜单
     * @param menuId
     * @return
     */
    SysMenu getSysMenuById(Long menuId);
}
