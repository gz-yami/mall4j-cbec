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


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.ProductDto;
import com.yami.shop.bean.app.dto.UserCollectionDto;
import com.yami.shop.bean.model.UserCollection;
import com.yami.shop.bean.vo.search.EsProductSearchVO;
import com.yami.shop.common.util.PageParam;

import java.util.List;

/**
 * 用户收藏表
 *
 * @author xwc
 * @date 2019-04-19 16:57:20
 */
public interface UserCollectionService extends IService<UserCollection> {

    /**
     * 分页获取用户收藏商品信息
     * @param page 分页参数
     * @param userId 用户id
     * @param prodName 商品名称
     * @param prodType 商品类型
     * @return 分页用户收藏商品信息
     */
    IPage<UserCollectionDto> getUserCollectionDtoPageByUserId(Page page, String userId,String prodName,Integer prodType);

    /**
     * 用户批量收藏或者取消收藏商品
     * @param prodIdList 商品id集合
     * @param userId 用户id
     */
    void orderProdCollectionAll(List<Long> prodIdList, String userId);

    /**
     * 获取用户收藏的商品列表
     * @param page
     * @param userId
     * @return
     */
    IPage<ProductDto> collectionProds(PageParam page, String userId);
}
