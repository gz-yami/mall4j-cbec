/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.WebConfig;
import com.yami.shop.bean.vo.WebConfigVO;
import com.yami.shop.common.bean.SysConfig;

/**
 *
 *
 * @author SJL
 * @date 2021-02-20 09:44:42
 */
public interface WebConfigService extends IService<WebConfig> {

    /**
     * 获取后台网站配置信息
     * @param paramKey 配置类型
     * @return 配置信息
     */
    WebConfigVO getActivityWebConfig(String paramKey);

    /**
     * 保存网页配置
     * @param sysConfig
     */
    void saveWebConfig(SysConfig sysConfig);

    /**
     * 获取指定的网页的配置数据
     * @param key 配置的key
     * @return 配置的value
     */
    String getValue(String key);

    /**
     * 获取指定的网页的配置数据
     * @param key 配置的key
     * @return
     */
    WebConfig info(String key);

}
