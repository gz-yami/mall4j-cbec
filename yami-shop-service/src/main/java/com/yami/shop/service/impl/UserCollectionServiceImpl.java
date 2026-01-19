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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.shop.bean.app.dto.ProductDto;
import com.yami.shop.bean.app.dto.UserCollectionDto;
import com.yami.shop.bean.model.ProdLang;
import com.yami.shop.bean.model.UserCollection;
import com.yami.shop.bean.param.EsProductParam;
import com.yami.shop.bean.vo.search.EsProductSearchVO;
import com.yami.shop.bean.vo.search.ProductSearchVO;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.dao.ProductMapper;
import com.yami.shop.dao.UserCollectionMapper;
import com.yami.shop.manager.impl.LangManager;
import com.yami.shop.service.UserCollectionService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户收藏表
 *
 * @author xwc
 * @date 2019-04-19 16:57:20
 */
@Service
@AllArgsConstructor
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

    private final UserCollectionMapper userCollectionMapper;
    private final ApplicationContext applicationContext;
    private final LangManager langManager;
    private final ProductMapper productMapper;

    @Override
    public IPage<UserCollectionDto> getUserCollectionDtoPageByUserId(Page page, String userId, String prodName, Integer prodType) {
        IPage<UserCollectionDto> userCollectionPage = userCollectionMapper.getUserCollectionDtoPageByUserId(page, userId, prodName, prodType);
        if (CollUtil.isEmpty(userCollectionPage.getRecords())) {
            return userCollectionPage;
        }
        List<Long> ids = userCollectionPage.getRecords().stream().map(UserCollectionDto::getProdId).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, ProdLang> prodLangMap = langManager.getProdLangMap(ids);
        for (UserCollectionDto item : userCollectionPage.getRecords()) {
            ProdLang prodLang = prodLangMap.get(item.getProdId());
            if (Objects.nonNull(prodLang)) {
                item.setProdName(prodLang.getProdName());
            }
        }
        return userCollectionPage;
    }

    @Override
    public void orderProdCollectionAll(List<Long> prodIdList, String userId) {
        List<UserCollection> userCollectionListDb = userCollectionMapper.selectList(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .in(UserCollection::getProdId, prodIdList));
        for (UserCollection userCollection:userCollectionListDb){
            prodIdList.remove(userCollection.getProdId());
        }
        if (CollUtil.isEmpty(prodIdList)) {
            return;
        }
        List<UserCollection> userCollectionList = Lists.newArrayList();
        for (Long prodId:prodIdList) {
            UserCollection userCollection = new UserCollection();
            userCollection.setUserId(userId);
            userCollection.setCreateTime(new Date());
            userCollection.setProdId(prodId);
            userCollectionList.add(userCollection);
        }
        saveBatch(userCollectionList);
    }

    @Override
    public IPage<ProductDto> collectionProds(PageParam page, String userId) {
        IPage<ProductDto> productDtoIPage = productMapper.collectionProds(page, userId);
        return productDtoIPage;
    }
}
