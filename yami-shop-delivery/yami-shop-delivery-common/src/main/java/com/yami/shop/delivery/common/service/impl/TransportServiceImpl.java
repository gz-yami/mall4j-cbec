/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.delivery.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.enums.ProdStatusEnums;
import com.yami.shop.bean.model.Area;
import com.yami.shop.bean.model.Product;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.delivery.common.dao.TranscityMapper;
import com.yami.shop.delivery.common.dao.TransfeeMapper;
import com.yami.shop.delivery.common.dao.TransportMapper;
import com.yami.shop.delivery.common.model.*;
import com.yami.shop.delivery.common.service.TransportService;
import com.yami.shop.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author lgh on 2018/11/16.
 */
@Service
@AllArgsConstructor
public class TransportServiceImpl extends ServiceImpl<TransportMapper, Transport> implements TransportService {

    private final TransportMapper transportMapper;

    private final TransfeeMapper transfeeMapper;

    private final TranscityMapper transcityMapper;

    private final ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertTransportAndTransfee(Transport transport) {

        // 插入运费模板
        transportMapper.insert(transport);
        // 插入所有的运费项和城市
        insertTransfeeAndTranscity(transport);

        // 插入所有的指定包邮条件项和城市
        if (transport.getHasFreeCondition() == 1) {
            insertTransfeeFreeAndTranscityFree(transport);
        }

        return transport.getTransportId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "TransportAndAllItems", key = "#transport.transportId")
    public void updateTransportAndTransfee(Transport transport) {
        TransportService transportService = (TransportService) AopContext.currentProxy();
        Transport dbTransport = transportService.getTransportAndAllItems(transport.getTransportId());

        // 删除所有的运费项
        transfeeMapper.deleteByTransportId(transport.getTransportId());
        // 删除所有的指定包邮条件项
        transfeeMapper.deleteTransfeeFreesByTransportId(transport.getTransportId());

        List<Long> transfeeIds = dbTransport.getTransfees().stream().map(Transfee::getTransfeeId).collect(Collectors.toList());
        List<Long> transfeeFreeIds = dbTransport.getTransfeeFrees().stream().map(TransfeeFree::getTransfeeFreeId).collect(Collectors.toList());


        // 删除所有运费项包含的城市
        transcityMapper.deleteBatchByTransfeeIds(transfeeIds);
        if(CollectionUtil.isNotEmpty(transfeeFreeIds)) {
            // 删除所有指定包邮条件项包含的城市
            transcityMapper.deleteBatchByTransfeeFreeIds(transfeeFreeIds);
        }

        // 更新运费模板
        transportMapper.updateById(transport);

        // 插入所有的运费项和城市
        insertTransfeeAndTranscity(transport);
        // 插入所有的指定包邮条件项和城市
        if (transport.getHasFreeCondition() == 1) {
            insertTransfeeFreeAndTranscityFree(transport);
        }
    }


    private void insertTransfeeFreeAndTranscityFree(Transport transport) {
        Long transportId = transport.getTransportId();
        List<TransfeeFree> transfeeFrees = transport.getTransfeeFrees();
        for (TransfeeFree transfeeFree : transfeeFrees) {
            transfeeFree.setTransportId(transportId);
        }
        // 批量插入指定包邮条件项 并返回指定包邮条件项 id，供下面循环使用
        transfeeMapper.insertTransfeeFrees(transfeeFrees);

        List<TranscityFree> transcityFrees = new ArrayList<>();
        for (TransfeeFree transfeeFree : transfeeFrees) {
            List<Area> cityList = transfeeFree.getFreeCityList();
            if (CollectionUtil.isEmpty(cityList)) {
                // 请选择指定包邮城市
                throw new YamiShopBindException("yami.selCity");
            }
            // 当地址不为空时
            for (Area area : cityList) {
                TranscityFree transcityParam = new TranscityFree();
                transcityParam.setTransfeeFreeId(transfeeFree.getTransfeeFreeId());
                transcityParam.setFreeCityId(area.getAreaId());
                transcityFrees.add(transcityParam);
            }
        }

        // 批量插入指定包邮条件项中的城市
        if (CollectionUtil.isNotEmpty(transcityFrees)) {
            transcityMapper.insertTranscityFrees(transcityFrees);
        }
    }

    private void insertTransfeeAndTranscity(Transport transport) {
        Long transportId = transport.getTransportId();
        List<Transfee> transfees = transport.getTransfees();
        for (Transfee transfee : transfees) {
            transfee.setTransportId(transportId);
        }
        // 批量插入运费项 并返回运费项id，供下面循环使用
        transfeeMapper.insertTransfees(transfees);

        List<Transcity> transcities = new ArrayList<>();
        for (Transfee transfee : transfees) {
            List<Area> cityList = transfee.getCityList();
            if (CollectionUtil.isEmpty(cityList)) {
                continue;
            }
            // 当地址不为空时
            for (Area area : cityList) {
                Transcity transcityParam = new Transcity();
                transcityParam.setTransfeeId(transfee.getTransfeeId());
                transcityParam.setCityId(area.getAreaId());
                transcities.add(transcityParam);
            }
        }

        // 批量插入运费项中的城市
        if (CollectionUtil.isNotEmpty(transcities)) {
            transcityMapper.insertTranscities(transcities);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransportAndTransfeeAndTranscity(Long[] ids) {


        TransportService transportService = (TransportService) AopContext.currentProxy();
        for (Long id : ids) {
            Transport dbTransport = transportService.getTransportAndAllItems(id);
            long count = productService.count(new LambdaQueryWrapper<Product>()
                    .eq(Product::getDeliveryTemplateId,id)
                    .ne(Product::getStatus, ProdStatusEnums.DELETE.getValue())
            );
            if (count>0){
                // 正在使用中，无法进行删除操作
                String message = I18nMessage.getMessage("yami.delivery.using");
                // 模板
                String model = I18nMessage.getMessage("yami.model");
                throw new YamiShopBindException(model+ dbTransport.getTransName() +message);
            }
            List<Long> transfeeIds = dbTransport.getTransfees().stream().map(Transfee::getTransfeeId).collect(Collectors.toList());
            // 删除所有运费项包含的城市
            transcityMapper.deleteBatchByTransfeeIds(transfeeIds);
            // 删除所有的运费项
            transfeeMapper.deleteByTransportId(id);
        }
        // 删除运费模板
        transportMapper.deleteTransports(ids);
    }


    @Override
    @Cacheable(cacheNames = "TransportAndAllItems", key = "#transportId")
    public Transport getTransportAndAllItems(Long transportId) {
        Transport transport = transportMapper.getTransportAndTransfeeAndTranscity(transportId);
        if (transport == null) {
            return null;
        }
        List<TransfeeFree> transfeeFrees = transportMapper.getTransfeeFreeAndTranscityFreeByTransportId(transportId);
        transport.setTransfeeFrees(transfeeFrees);
        return transport;
    }
    @Override
    @CacheEvict(cacheNames = "TransportAndAllItems", key = "#transportId")
    public void removeTransportAndAllItemsCache(Long transportId) {

    }
}
