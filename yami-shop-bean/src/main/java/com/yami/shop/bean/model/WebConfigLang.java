/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author SJL
 * @date 2021-02-20 13:25:13
 */
@Data
public class WebConfigLang implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 语言编号
     */
    private Integer lang;

    @Schema(description = "后台-版权声明" )
    private String bsCopyright;

    @Schema(description = "后台-标题文本" )
    private String bsTitleContent;

    @Schema(description = "后台-菜单展开时的文本" )
    private String bsMenuTitleOpen;

    @Schema(description = "后台-菜单缩小时的文本" )
    private String bsMenuTitleClose;

    @Schema(description = "PC-版权声明" )
    private String pcCopyright;

    @Schema(description = "PC-公司全名" )
    private String pcCompanyName;

    @Schema(description = "PC-底部公司地址等信息" )
    private String pcCompanyInfo;

    @Schema(description = "PC-网站标题内容" )
    private String pcTitleContent;

    @Schema(description = "PC-公司名简写" )
    private String pcCompanyNameShort;

    @Schema(description = "PC-导航栏欢迎语" )
    private String pcWelcome;

    @Schema(description = "h5-标题" )
    private String uniTitleContent;

    @Schema(description = "h5-商城欢迎语" )
    private String h5Welcome;
}
