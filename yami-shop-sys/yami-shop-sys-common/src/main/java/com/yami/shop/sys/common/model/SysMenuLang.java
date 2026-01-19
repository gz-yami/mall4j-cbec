/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单-国际化表
 *
 * @author YXF
 * @date 2021-08-09 09:51:55
 */
@Data
@TableName("tz_sys_menu_lang")
public class SysMenuLang implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 菜单id
     */
    private Long menuId;
    /**
     * 语言 0.通用 1.中文 2.英文
     */
    private Integer lang;
    /**
     * 菜单名称
     */
    private String name;

}
