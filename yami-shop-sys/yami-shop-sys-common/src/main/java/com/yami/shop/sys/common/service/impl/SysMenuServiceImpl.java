/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.common.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.common.bean.LangConfig;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.SysCacheNames;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.sys.common.constant.MenuType;
import com.yami.shop.sys.common.dao.SysMenuLangMapper;
import com.yami.shop.sys.common.dao.SysMenuMapper;
import com.yami.shop.sys.common.dao.SysRoleMenuMapper;
import com.yami.shop.sys.common.dao.SysUserMapper;
import com.yami.shop.sys.common.model.SysMenu;
import com.yami.shop.sys.common.model.SysMenuLang;
import com.yami.shop.sys.common.service.SysMenuService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh
 */
@Service("sysMenuService")
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysMenuLangMapper sysMenuLangMapper;
    private final LangManager langManager;
    private final SysUserMapper sysUserMapper;

    @Override
    public List<SysMenu> listMenuByUserId(Long userId) {
        // 用户的所有菜单信息
        List<SysMenu> sysMenus ;
        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN_ID) {
            sysMenus = sysMenuMapper.listMenu();
        } else {
            sysMenus = sysMenuMapper.listMenuByUserId(userId);
        }

        this.handleMenuLang(sysMenus);
        Map<Long, List<SysMenu>> sysMenuLevelMap = sysMenus.stream()
                .sorted(Comparator.comparing(SysMenu::getOrderNum))
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 一级菜单
        List<SysMenu> rootMenu = sysMenuLevelMap.get(0L);
        if (CollectionUtil.isEmpty(rootMenu)) {
            return Collections.emptyList();
        }
        // 二三级菜单
        for (SysMenu sysMenu : rootMenu) {
            List<SysMenu> secondList = sysMenuLevelMap.get(sysMenu.getMenuId());
            if (CollectionUtil.isEmpty(secondList)){
                sysMenu.setList(secondList);
                continue;
            }
            for (SysMenu secondMenu : secondList) {
                secondMenu.setList(sysMenuLevelMap.get(secondMenu.getMenuId()));
            }
            sysMenu.setList(secondList);
        }
        return rootMenu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuAndRoleMenu(Long menuId){
        // 删除菜单
        this.removeById(menuId);
        // 删除菜单与角色关联
        sysRoleMenuMapper.deleteByMenuId(menuId);
        // 删除菜单语言数据
        sysMenuLangMapper.deleteByMenuId(menuId);
    }


    @Override
    public List<Long> listMenuIdByRoleId(Long roleId) {
        return sysMenuMapper.listMenuIdByRoleId(roleId);
    }


    @Override
    public List<SysMenu> listSimpleMenuNoButton() {
        List<SysMenu> sysMenus = sysMenuMapper.listSimpleMenuNoButton();
        this.handleMenuLang(sysMenus);
        return sysMenus;
    }

    @Override
    public List<SysMenu> listRootMenu() {
        List<SysMenu> sysMenus = sysMenuMapper.listRootMenu();
        this.handleMenuLang(sysMenus);
        return sysMenus;
    }

    @Override
    public List<SysMenu> listChildrenMenuByParentId(Long parentId) {
        List<SysMenu> sysMenus = sysMenuMapper.listChildrenMenuByParentId(parentId);
        handleMenuLang(sysMenus);
        return sysMenus;
    }

    @Override
    public List<SysMenu> listMenuAndBtn(Long userId) {
        List<SysMenu> sysMenuList;
        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN_ID){
            sysMenuList = sysMenuMapper.listMenuAndBtn();
        }else {
            sysMenuList = sysMenuMapper.listRoleMenuAndBtn(userId);
        }
        handleMenuLang(sysMenuList);
        return sysMenuList;
    }

    @Override
    public Set<String> updateSonMenuState(Long menuId, Integer state) {
        sysMenuMapper.updateSonMenuState(menuId,state);
        Set<String> perms = sysMenuMapper.listMenuPerms(menuId);
        return perms;

    }

    @Override
    public Set<String> listConcealButtonPerms() {
        return sysMenuMapper.listConcealButtonPerms();
    }

    @Override
    public List<String> listMenuNameByPerms(String perms) {
        List<SysMenu> sysMenus = sysMenuMapper.listMenuNameByPerms(perms);
        if (CollUtil.isEmpty(sysMenus)) {
            return new ArrayList<>();
        }
        handleMenuLang(sysMenus);
        List<String> list = sysMenus.stream().map(SysMenu::getName).filter(name -> Objects.nonNull(name)).collect(Collectors.toList());
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSysMenu(SysMenu menu) {
        //数据校验
        verifyForm(menu);
        save(menu);
        // 保存菜单语言数据
        batchSaveMenuLang(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysMenu(SysMenu menu) {
        //数据校验
        verifyForm(menu);
        updateById(menu);
        // 先删除再保存新的菜单语言数据
        sysMenuLangMapper.deleteByMenuId(menu.getMenuId());
        batchSaveMenuLang(menu);
    }

    @Override
    public SysMenu getSysMenuById(Long menuId) {
        SysMenu sysMenu = sysMenuMapper.getSysMenuById(menuId);
        return sysMenu;
    }

    @Override
    @Cacheable(cacheNames = SysCacheNames.SYS_USER_PERMISSION, key = "#userId")
    public Set<String> getUserPermissions(Long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN_ID){
            List<SysMenu> menuList = sysMenuMapper.selectList(Wrappers.emptyWrapper());


            permsList = menuList.stream().map(SysMenu::getPerms).collect(Collectors.toList());
        }else{
            permsList = sysUserMapper.queryAllPerms(userId);
        }
        return permsList.stream().flatMap((perms)->{
                    if (StrUtil.isBlank(perms)) {
                        return null;
                    }
                    return Arrays.stream(perms.trim().split(StrUtil.COMMA));
                }
        ).collect(Collectors.toSet());
    }


    /**
     * 验证参数是否正确
     */
    private void verifyForm(SysMenu menu) {

        if (menu.getType() == MenuType.MENU.getValue()) {
            if (StrUtil.isBlank(menu.getUrl())) {
                // 菜单URL不能为空
                throw new YamiShopBindException("yami.sys.menu.exist");
            }
        }
        if (Objects.equals(menu.getMenuId(), menu.getParentId())) {
            // 自己不能是自己的上级
            throw new YamiShopBindException("yami.sys.menu.user.error");
        }

        if (!verifyLang(menu)) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.shop.menu.exception.langUpdate");
        }

        //上级菜单类型
        int parentType = MenuType.CATALOG.getValue();
        if (menu.getParentId() != 0) {
            SysMenu parentMenu = getById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == MenuType.CATALOG.getValue() ||
                menu.getType() == MenuType.MENU.getValue()) {
            if (parentType != MenuType.CATALOG.getValue()) {
                // 上级菜单只能为目录类型
                throw new YamiShopBindException("yami.sys.menu.superior.list");
            }
            return;
        }

        //按钮
        if (menu.getType() == MenuType.BUTTON.getValue()) {
            if ((parentType != 0) && parentType != MenuType.MENU.getValue()) {
                // 上级菜单不为空时，只能为菜单类型
                throw new YamiShopBindException("yami.sys.menu.superior.menu");
            }
        }
    }

    private boolean verifyLang(SysMenu sysMenu) {
        boolean hasDefaultLang = false;
        if (CollUtil.isEmpty(sysMenu.getMenuLangList())) {
            return hasDefaultLang;
        }
        Integer defaultLang = langManager.getDefaultLang();
        for (SysMenuLang sysMenuLang : sysMenu.getMenuLangList()) {
            if (Objects.equals(sysMenuLang.getLang(), defaultLang)) {
                hasDefaultLang = true;
            }
        }
        return hasDefaultLang;
    }

    private void handleMenuLang(List<SysMenu> sysMenuList) {
        this.handleMenuAndLang(sysMenuList);
    }
    private void handleMenuAndLang(List<SysMenu> sysMenuList) {
        if (CollUtil.isEmpty(sysMenuList)) {
            return;
        }
        Integer lang = I18nMessage.getLang();
        LangConfig langConfig = langManager.getLangConfig();
        for (SysMenu sysMenu : sysMenuList) {
            if (CollUtil.isEmpty(sysMenu.getMenuLangList())) {
                continue;
            }
            Map<Integer, SysMenuLang> menuLangMap = sysMenu.getMenuLangList().stream().collect(Collectors.toMap(SysMenuLang::getLang, s -> s));
            SysMenuLang sysMenuLang = menuLangMap.get(langManager.getLang(menuLangMap.keySet(), langConfig, lang));
            sysMenu.setName(sysMenuLang.getName());
        }
    }

    private void batchSaveMenuLang(SysMenu menu) {
        for (SysMenuLang sysMenuLang : menu.getMenuLangList()) {
            sysMenuLang.setMenuId(menu.getMenuId());
        }
        sysMenuLangMapper.batchSave(menu.getMenuLangList());
    }

}
