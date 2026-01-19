package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author gaozijie
 * @since 2024-03-25
 */
@Data
public class LangVO {

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

    @Schema(description = "默认语言id")
    private Integer defaultLangId;
}
