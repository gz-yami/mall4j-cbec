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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.ProdPropValueLang;
import com.yami.shop.dao.ProdPropValueLangMapper;
import com.yami.shop.service.ProdPropValueLangService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author yami
 * @date 2021-02-24 17:02:07
 */
@Service
@AllArgsConstructor
public class ProdPropValueLangServiceImpl extends ServiceImpl<ProdPropValueLangMapper, ProdPropValueLang> implements ProdPropValueLangService {

    private final ProdPropValueLangMapper prodPropValueLangMapper;
}
