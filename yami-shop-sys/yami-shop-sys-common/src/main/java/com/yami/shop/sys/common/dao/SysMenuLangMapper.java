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
import com.yami.shop.sys.common.model.SysMenuLang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统菜单-国际化表
 *
 * @author YXF
 * @date 2021-08-09 09:51:55
 */
public interface SysMenuLangMapper extends BaseMapper<SysMenuLang> {

    /**
     * 批量保存
     *
     * @param menuLangList
     */
    void batchSave(@Param("menuLangList") List<SysMenuLang> menuLangList);

    /**
     * 删除指定菜单的语言数据
     *
     * @param menuId
     */
    void deleteByMenuId(@Param("menuId") Long menuId);
}
