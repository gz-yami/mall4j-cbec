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

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.app.dto.DeliveryInfoDto;
import com.yami.shop.bean.bo.DeliveryHundredItemInfoBO;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.vo.DeliveryOrderSimpleVO;
import com.yami.shop.bean.vo.DeliveryOrderVO;
import com.yami.shop.common.bean.Quick100;
import com.yami.shop.common.bean.Tracking17;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.DeliveryMapper;
import com.yami.shop.service.DeliveryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lgh on 2018/11/26.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {

    private final DeliveryMapper deliveryMapper;

      @Override
    public List<DeliveryOrderVO> listDeliveryCountByOrderNumber(List<String> orderNumbers) {
        if (CollUtil.isEmpty(orderNumbers)) {
            return new ArrayList<>();
        }
        return deliveryMapper.listDeliveryCountByOrderNumber(orderNumbers);
    }

    @Override
    public Delivery getInfoById(Long deliveryId) {
        if (Objects.isNull(deliveryId)) {
            return null;
        }
        return this.getById(deliveryId);
    }

}
