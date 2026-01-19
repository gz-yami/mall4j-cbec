/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.WebConfig;
import com.yami.shop.bean.model.WebConfigLang;
import com.yami.shop.bean.vo.WebConfigVO;
import com.yami.shop.common.bean.SysConfig;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.WebConfigMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.SysConfigService;
import com.yami.shop.service.WebConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author SJL
 * @date 2021-02-20 09:44:42
 */
@Service
@AllArgsConstructor
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {

    private final SysConfigService sysConfigService;
    private final LangManager langManager;

    @Override
    public WebConfigVO getActivityWebConfig(String paramKey) {
        WebConfigVO webConfig  = sysConfigService.getSysConfigObject(paramKey, WebConfigVO.class);
        webConfig.setParamKey(paramKey);
        if (CollUtil.isEmpty(webConfig.getWebConfigLangList())) {
            return webConfig;
        }

        Map<Integer, WebConfigLang> webConfigLangMap = webConfig.getWebConfigLangList().stream().filter(webConfigLang -> Objects.nonNull(webConfigLang.getLang())).collect(Collectors.toMap(WebConfigLang::getLang, w -> w));
        WebConfigLang webConfigLang = webConfigLangMap.get(langManager.getLang(webConfigLangMap.keySet(), langManager.getLangConfig(), I18nMessage.getLang()));

        webConfig.setBsCopyright(webConfigLang.getBsCopyright());
        webConfig.setBsTitleContent(webConfigLang.getBsTitleContent());
        webConfig.setBsMenuTitleOpen(webConfigLang.getBsMenuTitleOpen());
        webConfig.setBsMenuTitleClose(webConfigLang.getBsMenuTitleClose());
        webConfig.setPcCopyright(webConfigLang.getPcCopyright());
        webConfig.setPcCompanyName(webConfigLang.getPcCompanyName());
        webConfig.setPcCompanyInfo(webConfigLang.getPcCompanyInfo());
        webConfig.setPcTitleContent(webConfigLang.getPcTitleContent());
        webConfig.setPcCompanyNameShort(webConfigLang.getPcCompanyNameShort());
        webConfig.setPcWelcome(webConfigLang.getPcWelcome());
        webConfig.setUniTitleContent(webConfigLang.getUniTitleContent());

        return webConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWebConfig(SysConfig sysConfig) {
        // 检验
        checkWebConfig(sysConfig);
        String paramValue = sysConfig.getParamValue();
        String paramKey = sysConfig.getParamKey();
        if (Objects.isNull(paramKey) || Objects.isNull(paramValue)) {
            // 参数不完整，请准确填写后重试
            throw new YamiShopBindException("yami.web.config.exception.paramLoss");
        }
        long count = sysConfigService.count(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getParamKey, paramKey));
        if (count > 0) {
            sysConfigService.updateValueByKey(paramKey, paramValue);
        } else {
            sysConfigService.save(sysConfig);
            sysConfigService.removeSysConfig(paramKey);
        }
    }

    private void checkWebConfig(SysConfig sysConfig) {
        WebConfig webConfig = Json.parseObject(sysConfig.getParamValue(), WebConfig.class);
        if (CollUtil.isEmpty(webConfig.getWebConfigLangList())) {
            // 语言列表不能为空
            throw new YamiShopBindException("yami.web.config.exception.langEmpty");
        }
        boolean containsMaster = handldWebConfig(sysConfig, webConfig);
        if (!containsMaster) {
            // 语言数据已更新，请重新录入商品信息
            throw new YamiShopBindException("yami.web.config.exception.langUpdate");
        }
        // 后台-版权声明
        webConfig.setBsCopyright(null);
        // 后台-标题文本
        webConfig.setBsTitleContent(null);
        // 后台-菜单展开时的文本
        webConfig.setBsMenuTitleOpen(null);
        // 后台-菜单缩小时的文本
        webConfig.setBsMenuTitleClose(null);
        // PC-版权声明
        webConfig.setPcCopyright(null);
        // PC-公司全名
        webConfig.setPcCompanyName(null);
        // PC-底部公司地址等信息
        webConfig.setPcCompanyInfo(null);
        // PC-网站标题内容
        webConfig.setPcTitleContent(null);
        // PC-公司名简写
        webConfig.setPcCompanyNameShort(null);
        // PC-导航栏欢迎语
        webConfig.setPcWelcome(null);
        // h5-标题
        webConfig.setUniTitleContent(null);
        sysConfig.setParamValue(Json.toJsonString(webConfig));
    }

    private boolean handldWebConfig(SysConfig sysConfig, WebConfig webConfig) {
        Integer lang = langManager.getDefaultLang();
        boolean containsMaster = false;
        for (WebConfigLang webConfigLang : webConfig.getWebConfigLangList()) {
            if (Objects.equals(webConfigLang.getLang(), lang)) {
                containsMaster = true;
            }
            // 去除空字符串
            // 后台-版权声明
            if (StrUtil.isBlank(webConfigLang.getBsCopyright())) {
                webConfigLang.setBsCopyright(null);
            }
            // 后台-标题文本
            if (StrUtil.isBlank(webConfigLang.getBsTitleContent())) {
                webConfigLang.setBsTitleContent(null);
            }
            // 后台-菜单展开时的文本
            if (StrUtil.isBlank(webConfigLang.getBsMenuTitleOpen())) {
                webConfigLang.setBsMenuTitleOpen(null);
            }
            // 后台-菜单缩小时的文本
            if (StrUtil.isBlank(webConfigLang.getBsMenuTitleClose())) {
                webConfigLang.setBsMenuTitleClose(null);
            }
            // PC-版权声明
            if (StrUtil.isBlank(webConfigLang.getPcCopyright())) {
                webConfigLang.setPcCopyright(null);
            }
            // PC-公司全名
            if (StrUtil.isBlank(webConfigLang.getPcCompanyName())) {
                webConfigLang.setPcCompanyName(null);
            }
            // PC-底部公司地址等信息
            if (StrUtil.isBlank(webConfigLang.getPcCompanyInfo())) {
                webConfigLang.setPcCompanyInfo(null);
            }
            // PC-网站标题内容
            if (StrUtil.isBlank(webConfigLang.getPcTitleContent())) {
                webConfigLang.setPcTitleContent(null);
            }
            if (StrUtil.isBlank(webConfigLang.getBsCopyright())) {
                webConfigLang.setBsCopyright(null);
            }
            // PC-公司名简写
            if (StrUtil.isBlank(webConfigLang.getPcCompanyNameShort())) {
                webConfigLang.setPcCompanyNameShort(null);
            }
            // PC-导航栏欢迎语
            if (StrUtil.isBlank(webConfigLang.getPcWelcome())) {
                webConfigLang.setPcWelcome(null);
            }
            // h5-标题
            if (StrUtil.isBlank(webConfigLang.getUniTitleContent())) {
                webConfigLang.setUniTitleContent(null);
            }
        }
        return containsMaster;
    }

    @Override
    public String getValue(String key) {
        return sysConfigService.getValue(key);
    }

    @Override
    public WebConfig info(String key) {
        String value = sysConfigService.getValue(key);
        if (StrUtil.isBlank(value)) {
            return new WebConfig();
        }
        WebConfig webConfig = Json.parseObject(value, WebConfig.class);
        if (CollUtil.isEmpty(webConfig.getWebConfigLangList())) {
            return webConfig;
        }
        Map<Integer, WebConfigLang> webConfigLangMap = webConfig.getWebConfigLangList().stream().filter(webConfigLang -> Objects.nonNull(webConfigLang.getLang())).collect(Collectors.toMap(WebConfigLang::getLang, w -> w));
        WebConfigLang webConfigLang = webConfigLangMap.get(langManager.getLang(webConfigLangMap.keySet()));
        webConfig.setBsCopyright(webConfigLang.getBsCopyright());
        webConfig.setBsTitleContent(webConfigLang.getBsTitleContent());
        webConfig.setBsMenuTitleOpen(webConfigLang.getBsMenuTitleOpen());
        webConfig.setBsMenuTitleClose(webConfigLang.getBsMenuTitleClose());
        webConfig.setPcCopyright(webConfigLang.getPcCopyright());
        webConfig.setPcCompanyName(webConfigLang.getPcCompanyName());
        webConfig.setPcCompanyInfo(webConfigLang.getPcCompanyInfo());
        webConfig.setPcTitleContent(webConfigLang.getPcTitleContent());
        webConfig.setPcCompanyNameShort(webConfigLang.getPcCompanyNameShort());
        webConfig.setPcWelcome(webConfigLang.getPcWelcome());
        webConfig.setUniTitleContent(webConfigLang.getUniTitleContent());
        return webConfig;
    }
}
