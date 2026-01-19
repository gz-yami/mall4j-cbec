package com.yami.shop.common.i18n.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author gaozijie
 * @since 2024-04-12
 */
@Data
public class I18nLang {

    @Schema(description = "id")
    private Integer langId;

    @Schema(description = "语言名称")
    private String name;

    @Schema(description = "语言编号")
    private Integer lang;

    @Schema(description = "语言代码")
    private String language;

    @Schema(description = "是否默认")
    private Integer master;

    @Schema(description = "是否隐藏")
    private Integer hide;

    @Schema(description = "备注")
    private String remark;

}
