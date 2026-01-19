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

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.param.ScoreConfigParam;
import com.yami.shop.common.bean.*;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.SysConfigMapper;
import com.yami.shop.service.SysConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author lgh
 */
@Slf4j
@Service("sysConfigService")
@AllArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    private final ApplicationContext applicationContext;

    private final StringRedisTemplate stringRedisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames="SysConfigObject",key="#key"),
            @CacheEvict(cacheNames="SysConfig",key="#key")
    })
    public void updateValueByKey(String key, String value) {
        sysConfigMapper.updateValueByKey(key, value);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysConfigMapper.deleteBatch(ids);
    }

    @Override
    @Cacheable(cacheNames = "SysConfig", key = "#key")
    public String getValue(String key) {
        SysConfig config = sysConfigMapper.queryByKey(key);
        return config == null ? null : config.getParamValue();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames="SysConfigObject",key="#key"),
            @CacheEvict(cacheNames="SysConfig",key="#key")
    })
    public void removeSysConfig(String key) {
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames="SysConfigObject",key="#key"),
            @CacheEvict(cacheNames="SysConfig",key="#key")
    })
    public void saveOrUpdateSysConfigService(ScoreConfigParam scoreConfigParam, String key) {
        SysConfig config = new SysConfig();
        List<Integer> signInScore = scoreConfigParam.getSignInScore();
        if(CollectionUtils.isNotEmpty(signInScore)) {
            StringBuilder signsString = new StringBuilder();
            for (Integer score : signInScore) {
                signsString.append(score).append(StrUtil.COMMA);
            }
            signsString.deleteCharAt(signsString.length() - 1);
            scoreConfigParam.setSignInScoreString(signsString.toString());
            scoreConfigParam.setSignInScore(null);
        }
        String paramValue = Json.toJsonString(scoreConfigParam);
        config.setParamKey(Constant.SCORE_CONFIG);
        config.setParamValue(paramValue);
        config.setRemark("积分获取比例和积分抵现规则");
        if (sysConfigMapper.selectCount(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getParamKey, config.getParamKey())) > 0) {
            sysConfigMapper.update(config,new LambdaUpdateWrapper<SysConfig>().eq(SysConfig::getParamKey,config.getParamKey()));
        }
        else{
            sysConfigMapper.insert(config);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> saveDeliveryConfig(SysConfig sysConfig) {
        // 失效的缓存keys
        List<String> invalidKeys = new ArrayList<>();
        SwitchBaseModel switchBaseModel = Json.parseObject(sysConfig.getParamValue(), SwitchBaseModel.class);
        if (BooleanUtil.isTrue(switchBaseModel.getIsOpen())) {
            // 关闭其他物流查询配置
            if (!Objects.equals(sysConfig.getParamKey(), Constant.QUICK100_CONFIG)) {
                // 关闭快递100查询
                this.closeSwitch(Constant.QUICK100_CONFIG, Quick100.class, invalidKeys);
            }
            // 如果有新增的物流查询配置，在这里补上关闭的代码
            if (!Objects.equals(sysConfig.getParamKey(), Constant.SEVENTEEN_TRACK_CONFIG)) {
                // 关闭快递17tracking查询
                this.closeSwitch(Constant.SEVENTEEN_TRACK_CONFIG, Quick100.class, invalidKeys);
            }
        }
        // 保存当前物流查询配置
        this.saveOrUpdateSysConfigByKey(sysConfig);
        invalidKeys.add(sysConfig.getParamKey());
        return invalidKeys;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames="SysConfigObject",key="#sysConfig.paramKey"),
            @CacheEvict(cacheNames="SysConfig",key="#sysConfig.paramKey")
    })
    public void saveOrUpdateSysConfigByKey(SysConfig sysConfig) {
        long count = sysConfigMapper.selectCount(Wrappers.lambdaQuery(SysConfig.class)
                .eq(SysConfig::getParamKey, sysConfig.getParamKey())
        );
        if (count > 0) {
            sysConfigMapper.updateValueByKey(sysConfig.getParamKey(), sysConfig.getParamValue());
        } else {
            sysConfig.setId(null);
            sysConfigMapper.insert(sysConfig);
        }
    }

    /**
     * 关闭激活开关
     * @param key 配置项key
     * @param clazz 继承了SwitchBaseModel的类
     * @param invalidKeys 缓存失效的配置集合
     * @param <T>
     */
    public <T> void closeSwitch(String key, Class<T> clazz, List<String> invalidKeys) {
        if (!SwitchBaseModel.class.isAssignableFrom(clazz)) {
            // 不是配置开关属性类的子类不能调用该方法
            return;
        }
        SysConfigService sysConfigService = (SysConfigService) AopContext.currentProxy();
        T valueObj = sysConfigService.getSysConfigObject(key, clazz);
        if (Objects.isNull(valueObj)) {
            // 当前配置在数据库不存在
            return;
        }
        try {
            PropertyDescriptor pd = new PropertyDescriptor("isOpen", clazz);
            Method readMethod = pd.getReadMethod();
            if (BooleanUtil.isFalse((Boolean) readMethod.invoke(valueObj))) {
                // isOpen已经是关闭状态，直接返回
                return;
            }
            Method writeMethod = pd.getWriteMethod();
            // 关闭当前配置
            writeMethod.invoke(valueObj, false);
        } catch (Exception e) {
            log.error(clazz + "类配置存在问题: ", e);
            return;
        }
        String str = Json.toJsonString(valueObj);
        sysConfigMapper.updateValueByKey(key, str);
        invalidKeys.add(key);
    }

    @Override
    @Cacheable(cacheNames="SysConfigObject",key="#key")
    public <T> T  getSysConfigObject(String key, Class<T> clazz) {
        String value = getValue(key);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String className = "java.lang.String";
        if(className.equals(clazz.getName())){
            return (T)value;
        }else{
            return Json.parseObject(value, clazz);
        }
    }
}
