/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yami.shop.bean.model.WebConfigLang;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @author SJL
 * @date 2021-02-20 13:25:13
 */
@Data
public class WebConfigVO implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 配置类型（PLATFROM:平台端   MULTISHOP:商家端   PC:PC端   H5:h5端   STATION:自提点端）
     * @see com.yami.shop.bean.enums.WebConfigTypeEnum 配置类型枚举类
     */
    @Schema(description = "配置类型" )
    private String paramKey;

    @Schema(description = "激活（0:否 1:是）" )
    private Integer isActivity;

    @Schema(description = "后台-登录logo" )
    private String bsLoginLogoImg;

    @Schema(description = "后台-登录背景" )
    private String bsLoginBgImg;

    @Schema(description = "后台-版权声明" )
    private String bsCopyright;

    @Schema(description = "后台-标题文本" )
    private String bsTitleContent;

    @Schema(description = "后台-标题图标" )
    private String bsTitleImg;

    @Schema(description = "后台-菜单展开时的文本" )
    private String bsMenuTitleOpen;

    @Schema(description = "后台-菜单缩小时的文本" )
    private String bsMenuTitleClose;

    @Schema(description = "PC-登录logo" )
    private String pcLogoImg;

    @Schema(description = "PC-版权声明" )
    private String pcCopyright;

    @Schema(description = "PC-底部二维码" )
    private String pcQrcodeImg;

    @Schema(description = "PC-公司全名" )
    private String pcCompanyName;

    @Schema(description = "PC-底部公司地址等信息" )
    private String pcCompanyInfo;

    @Schema(description = "PC-网站标题内容" )
    private String pcTitleContent;

    @Schema(description = "PC-网站标题图标" )
    private String pcTitleImg;

    @Schema(description = "PC-公司名简写" )
    private String pcCompanyNameShort;

    @Schema(description = "PC-公司图文logo（注册、登录、改密时显示）" )
    private String pcLogoImgText;

    @Schema(description = "PC-导航栏欢迎语" )
    private String pcWelcome;

    @Schema(description = "PC-登录背景" )
    private String pcLoginBgImg;

    @Schema(description = "h5-登录logo" )
    private String uniLoginLogoImg;

    @Schema(description = "Station-自提点登录logo" )
    private String stationLoginLogoImg;

    @Schema(description = "后台-顶部栏图标" )
    private String bsTopBarIcon;

    @Schema(description = "h5-标题" )
    private String uniTitleContent;

    @Schema(description = "是否开启商家注册协议" )
    @TableField(exist = false)
    private Boolean merchantRegisterProtocolSwitch;

    @Schema(description = "是否开启商家注册协议" )
    @TableField(exist = false)
    private List<WebConfigLang> webConfigLangList;
}
