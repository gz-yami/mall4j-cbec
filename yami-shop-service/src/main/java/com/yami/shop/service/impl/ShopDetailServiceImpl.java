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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.event.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.vo.ShopDetailVO;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.util.*;
import com.yami.shop.dao.ProductMapper;
import com.yami.shop.dao.ShopDetailMapper;
import com.yami.shop.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author lgh on 2018/08/29.
 */
@Service
public class ShopDetailServiceImpl extends ServiceImpl<ShopDetailMapper, ShopDetail> implements ShopDetailService {
    private static final Logger log = LoggerFactory.getLogger(ShopDetailServiceImpl.class);

    @Autowired
    private ShopDetailMapper shopDetailMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    @Lazy
    private ShopCompanyService shopCompanyService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = CacheNames.SHOP_DETAIL_ID_KEY, key = "#shopDetail.shopId")
    public void updateShopDetail(ShopDetail shopDetail) {
        // 若经纬度数据变更，需清空同城配送中的位置信息
        ShopDetail detailDb = this.getById(shopDetail.getShopId());
        if (!PrincipalUtil.isDbPhone(shopDetail.getTel(), detailDb.getTel(), true)) {
            throw new YamiShopBindException("yami.user.err.phone");
        }
        if (shopDetail.getTel().contains(Constant.ASTERISK)) {
            shopDetail.setTel(detailDb.getTel());
        }
//        boolean checkLocation = detailDb.getShopLng() != null && detailDb.getShopLat() != null;
//        boolean check = checkLocation && (!detailDb.getShopLat().equals(shopDetail.getShopLat()) || !detailDb.getShopLng().equals(shopDetail.getShopLng()));
//        if (check) {
//            applicationContext.publishEvent(new ClearPolygonPathEvent(shopDetail.getShopId()));
//        }
        updateById(shopDetail);
    }

    @Override
    public double getDistance(Double lat, Double lng, Long shopId) {
        return shopDetailMapper.getDistance(lat, lng, shopId);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.SHOP_DETAIL_ID_KEY, key = "#shopId")
    public ShopDetail getShopDetailByShopId(Long shopId) {
        ShopDetail shopDetail = shopDetailMapper.selectShopDetailById(shopId);
        shopDetail.setIsPassShop(1);
        boolean shopTypeCheck = Objects.nonNull(shopDetail) && !Objects.equals(shopDetail.getShopId(),Constant.PLATFORM_SHOP_ID);
        return shopDetail;
    }


    @Override
    @CacheEvict(cacheNames = CacheNames.SHOP_DETAIL_ID_KEY, key = "#shopId")
    public void removeShopDetailCacheByShopId(Long shopId) {
    }

    @Override
    public ShopDetail getShopByMobile(String mobile) {
        return shopDetailMapper.getShopByMobile(mobile);
    }

    @Override
    public ShopDetail getShopDetailByUserId(String userId) {
        ShopDetail shopDetail = shopDetailMapper.getShopDetailByUserId(userId);
        boolean shopTypeCheck = Objects.nonNull(shopDetail) && !Objects.equals(shopDetail.getShopId(),Constant.PLATFORM_SHOP_ID);
        return shopDetail;
    }


    @Override
    public long checkMobile(String mobile, Long shopId) {
        return count(new LambdaQueryWrapper<ShopDetail>()
                .eq(ShopDetail::getMobile, mobile)
                .ne(Objects.nonNull(shopId), ShopDetail::getShopId, shopId)
        );
    }


    /**
     * 根据店铺id列表删除缓存
     *
     * @param shopIds
     */
    private void removeCacheByShopIds(List<Long> shopIds) {
        List<String> keyList = new ArrayList<>();
        shopIds.forEach(shopId -> {
            keyList.add(CacheNames.SHOP_DETAIL_ID_KEY + CacheNames.UNION + shopId);
        });
        RedisUtil.deleteBatch(keyList);
    }


    @Override
    public List<ShopDetail> getShopDetailByShopIds(List<Long> shopIds) {
        return shopDetailMapper.getShopDetailByShopIds(shopIds);
    }



    private Date getSaveEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.endOfDay(date));
        // 防止时间进位
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * 发送事件提醒es更新商品信息
     *
     * @param shopIds
     */
    private void updateProdByShopIds(List<Long> shopIds) {
        List<Long> prodIds = productMapper.listIdByShopIds(shopIds);
        if (CollUtil.isEmpty(prodIds)) {
            return;
        }
        eventPublisher.publishEvent(new EsProductUpdateEvent(null, prodIds, EsOperationType.UPDATE_BATCH));
    }

    private List<String> getExcelHeader() {
        return Arrays.asList(
                I18nMessage.getMessage("yami.shop.detail.excel.header.shopName"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.businessName"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.status"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.phone"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.intro"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.applyTime"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.type"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.signStartTime"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.signEndTime"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.scope"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.socialCode"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.companyName"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.domicile"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.legalPerson"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.registerPrice"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.registerTime"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.businessStartTime"),
                I18nMessage.getMessage("yami.shop.detail.excel.header.businessEndTime")
        );
    }
}
