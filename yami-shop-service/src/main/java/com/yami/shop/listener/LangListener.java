package com.yami.shop.listener;

import com.yami.shop.bean.vo.LangVO;
import com.yami.shop.common.i18n.bean.I18nLang;
import com.yami.shop.common.i18n.event.GetLangEvent;
import com.yami.shop.service.LangService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author gaozijie
 * @since 2024-04-12
 */
@Component
@AllArgsConstructor
public class LangListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LangService langService;

    @EventListener(GetLangEvent.class)
    public void getLang(GetLangEvent event) {
        Map<String, LangVO> langMap = langService.getLangMap();
        // 获取指定语言
        if (!ObjectUtils.isEmpty(event.getLanguage())) {
            LangVO langVO = langMap.get(event.getLanguage());
            I18nLang i18nLang = new I18nLang();
            if (Objects.isNull(langVO)) {
                logger.error("language: {}，没有找到对应的语言!", event.getLanguage());
            } else {
                BeanUtils.copyProperties(langVO, i18nLang);
            }
            event.setI18nLang(i18nLang);
        }
        // 补充默认语言，LinkedHashMap，第一条数据是默认语言
        I18nLang defaultI18nLang = new I18nLang();
        LangVO defaultLang = langMap.entrySet().iterator().next().getValue();
        BeanUtils.copyProperties(defaultLang, defaultI18nLang);
        event.setDefaultI18nLang(defaultI18nLang);
    }
}
