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
import com.yami.shop.bean.param.ScoreConfigParam;
import com.yami.shop.common.bean.SysConfig;

import java.util.List;

/**
 * 系统配置信息
 * @author lgh
 */
public interface SysConfigService extends IService<SysConfig>  {

    /**
     * 根据key，更新value
     * @param key 参数key
     * @param value 参数value
     */
    void updateValueByKey(String key, String value);

    /**
     * 删除配置信息
     * @param ids 配置项id列表
     */
    void deleteBatch(Long[] ids);

    /**
     * 根据key，获取配置的value值
     * @param key 参数key
     * @return value
     */
    String getValue(String key);


    /**
     * 获取配置信息，并返回对应的类
     * @param key key
     * @param clazz 类
     * @param <T> 泛型
     * @return 泛型
     */
    <T> T  getSysConfigObject(String key, Class<T> clazz);

    /**
     * 删除key的配置信息
     * @param key key
     */
    void removeSysConfig(String key);

    /**
     * 保存或者修改key的配置信息
     * @param scoreConfigParam 积分配置信息
     * @param key key
     */
    void saveOrUpdateSysConfigService(ScoreConfigParam scoreConfigParam, String key);

    /**
     * 保存物流查询配置信息
     * @param sysConfig
     * @return 失效的keys
     */
    List<String> saveDeliveryConfig(SysConfig sysConfig);

    /**
     * 根据是否已经存在以key为名称的配置进行保存或更新
     * @param sysConfig
     */
    void saveOrUpdateSysConfigByKey(SysConfig sysConfig);
}
