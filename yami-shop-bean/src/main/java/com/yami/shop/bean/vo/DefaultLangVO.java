package com.yami.shop.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author gaozijie
 * @since 2024-03-26
 */
@Getter
@Setter
@ToString
public class DefaultLangVO extends LangVO{

    @Schema(description = "多语言项")
    private List<LangVO> langItemList;
}
