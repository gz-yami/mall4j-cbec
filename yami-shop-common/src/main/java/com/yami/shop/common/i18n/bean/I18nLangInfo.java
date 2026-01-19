package com.yami.shop.common.i18n.bean;

import lombok.Data;

/**
 * @author gaozijie
 * @since 2024-04-12
 */
@Data
public class I18nLangInfo {

    /**
     * 当前语言
     */
    private I18nLang currentLang;

    /**
     * 默认语言
     */
    private I18nLang defaultLang;
}
