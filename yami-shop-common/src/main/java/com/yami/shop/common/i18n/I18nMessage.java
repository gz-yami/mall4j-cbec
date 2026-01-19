/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.i18n;


import com.yami.shop.common.i18n.event.GetLangEvent;
import com.yami.shop.common.i18n.bean.I18nLangInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

/**
 * 多语言国际化消息工具类
 * @author yami
 */
@Slf4j
@Component
public class I18nMessage {
    private static final MessageSourceAccessor ACCESSOR;

    private static final String BASE_NAME = "i18n/messages";

    private static ApplicationContext applicationContext;

    static{
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasenames(BASE_NAME);
        reloadableResourceBundleMessageSource.setCacheSeconds(5);
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        ACCESSOR = new MessageSourceAccessor(reloadableResourceBundleMessageSource);
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        I18nMessage.applicationContext = applicationContext;
    }

    /**
     * 获取一条语言配置信息
     *
     * @param message 配置信息属性名,eg: api.response.code.user.signUp
     * @return
     */
    public static String getMessage(String message) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return ACCESSOR.getMessage(message,locale);
        }catch (Exception e){
            return message;
        }

    }
    /**
     * 获取一条语言配置信息（后台管理）
     *
     * @return
     * @throws IOException
     */
    public static Integer getLang() {
        GetLangEvent event = getLangEvent();
        return event.getI18nLang().getLang();
    }

    /**
     * 获取一条语言配置信息(小程序、pc)
     *
     * @return
     * @throws IOException
     */
    public static Integer getDbLang() {
        return getLang();
    }

    /**
     * 获取当前语言信息
     * @return 当前语言信息
     */
    public static I18nLangInfo getI18nLang() {
        GetLangEvent event= getLangEvent();
        I18nLangInfo i18nLangInfo = new I18nLangInfo();
        i18nLangInfo.setCurrentLang(event.getI18nLang());
        i18nLangInfo.setDefaultLang(event.getDefaultI18nLang());
        return i18nLangInfo;
    }

    private static GetLangEvent getLangEvent() {
        Locale locale = LocaleContextHolder.getLocale();
        GetLangEvent event = new GetLangEvent(locale.toString());
        applicationContext.publishEvent(event);
        return event;
    }
}
