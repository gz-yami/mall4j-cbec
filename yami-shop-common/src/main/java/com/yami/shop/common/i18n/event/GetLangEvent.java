package com.yami.shop.common.i18n.event;

import com.yami.shop.common.i18n.bean.I18nLang;
import lombok.Data;

/**
 * @author gaozijie
 * @since 2024-04-12
 */
@Data
public class GetLangEvent {

    /**
     * 当前语言
     */
    private I18nLang i18nLang;

    /**
     * 默认语言
     */
    private I18nLang defaultI18nLang;

    /**
     * 语言代码
     */
    private String language;

    public GetLangEvent(String language) {
        this.language = language;
    }
}
