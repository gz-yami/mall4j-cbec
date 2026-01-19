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
import com.yami.shop.bean.model.ShopCompany;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.vo.ShopCompanyVO;

/**
 * @Author lth
 * @Date 2021/8/3 16:16
 */
public interface ShopCompanyService extends IService<ShopCompany> {

    /**
     * 获取详情
     * @param shopId 店铺id
     * @param status 状态
     * @return 店铺工商信息VO
     */
    ShopCompanyVO getInfo(Long shopId, Integer status);

    /**
     * 保存店铺工商信息
     * @param shopCompany
     */
    void saveInfo(ShopCompany shopCompany);

    /**
     * 根据店铺id更新工商信息
     * @param shopCompany
     */
    void updateByShopId(ShopCompany shopCompany);

    /**
     * 根据店铺id和状态获取工商信息
     * @param shopId
     * @param status
     * @return
     */
    ShopCompanyVO getShopCompanyByShopIdAndStatus(Long shopId, Integer status);

    /**
     * 检查信用代码是否重复
     * @param creditCode
     * @param shopId
     * @return
     */
    Boolean checkCreditCode(String creditCode, Long shopId);
}
