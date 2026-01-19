/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.dto.CurrencyDTO;
import com.yami.shop.bean.model.Currency;
import com.yami.shop.bean.vo.CurrencyLangVO;
import com.yami.shop.bean.vo.CurrencyVO;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *
 * @author LGH
 */
public interface CurrencyMapper extends BaseMapper<Currency> {

    /**
     * 获取启用的货币信息
     *
     * @return
     */
    List<CurrencyVO> listEnableCurrency();

    /**
     * 获取货币信息
     *
     * @param id
     * @return
     */
    CurrencyVO getCurrencyById(@Param("id") Long id);

    /**
     * 分页获取语言信息
     *
     * @param pageParam
     * @param currencyDTO
     * @return
     */
    IPage<CurrencyVO> pageCurrency(PageParam<CurrencyVO> pageParam, @Param("currencyDTO") CurrencyDTO currencyDTO);
}
