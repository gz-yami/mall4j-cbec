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
import cn.hutool.core.map.MapUtil;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.dto.TransportDto;
import com.yami.shop.bean.enums.DeliveryTemplateType;
import com.yami.shop.bean.enums.ProdType;
import com.yami.shop.bean.enums.TransportChargeType;
import com.yami.shop.bean.model.Area;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.bean.vo.ShopTransFeeVO;
import com.yami.shop.bean.vo.UserDeliveryInfoVO;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.delivery.common.constant.TransportFreeType;
import com.yami.shop.delivery.common.model.Transfee;
import com.yami.shop.delivery.common.model.TransfeeFree;
import com.yami.shop.delivery.common.model.Transport;
import com.yami.shop.delivery.common.service.TransportManagerService;
import com.yami.shop.delivery.common.service.TransportService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Lanhai
 */
@Service
@AllArgsConstructor
public class TransportManagerServiceImpl implements TransportManagerService {

    private final TransportService transportService;

    @Override
    public double calculateTransFee(List<ShopCartItemDto> productItems, UserAddr userAddr, UserDeliveryInfoVO userDeliveryInfoVO) {
        Map<Long, ShopTransFeeVO> shopIdWithShopTransFee = new HashMap<>(16);

        // 商品的总运费
        double totalTransFee = 0.0;

        // 去除没有店铺配送的订单和运费模板的订单
        List<ShopCartItemDto> transPortList = removeNotTransPort(productItems);
        if (CollectionUtils.isEmpty(transPortList)) {
            return 0.0;
        }

        Map<Long, List<ShopCartItemDto>> deliveryTemplateIdWithShopCartItems = transPortList.stream().
                collect(Collectors.groupingBy(ShopCartItemDto::getDeliveryTemplateId));

        Map<Long, TransportDto> transportDtoMap;
        if (Objects.nonNull(userDeliveryInfoVO) && MapUtil.isNotEmpty(userDeliveryInfoVO.getTransportMap())) {
            transportDtoMap = userDeliveryInfoVO.getTransportMap();
        } else {
            transportDtoMap = Collections.emptyMap();
        }

        for (Long deliveryTemplateId : deliveryTemplateIdWithShopCartItems.keySet()) {
            List<ShopCartItemDto> shopCartItemDtoList = deliveryTemplateIdWithShopCartItems.get(deliveryTemplateId);
            // 统一运费、统一包邮的运费计算
            if (Objects.equals(deliveryTemplateId, DeliveryTemplateType.FREE_SHIPPING.getValue()) || Objects.equals(deliveryTemplateId, DeliveryTemplateType.FREIGHT.getValue())) {
                double transFee = calculateUnifiedFreight(deliveryTemplateId, shopCartItemDtoList, shopIdWithShopTransFee);
                totalTransFee = Arith.add(totalTransFee, transFee);
                continue;
            }

            // 普通运费模板-计算运费模板的运费
            double transFee = repeatListTransFee(deliveryTemplateId, shopCartItemDtoList, userAddr, shopIdWithShopTransFee, transportDtoMap);
            totalTransFee = Arith.add(totalTransFee, transFee);
        }

        if (Objects.nonNull(userDeliveryInfoVO)) {
            if(userDeliveryInfoVO.getShopIdWithShopTransFee() != null && userDeliveryInfoVO.getShopIdWithShopTransFee().size() != 0){
                userDeliveryInfoVO.getShopIdWithShopTransFee().putAll(shopIdWithShopTransFee);
            }else{
                userDeliveryInfoVO.setShopIdWithShopTransFee(shopIdWithShopTransFee);
            }
        }
        return totalTransFee;
    }


    /**
     * 去除没有店铺后的订单
     *
     * @param productItems 原用户所有的未处理订单
     * @return 去除没有店铺后的订单
     */
    private List<ShopCartItemDto> removeNotTransPort(List<ShopCartItemDto> productItems) {
        // 保存去掉没有运费模板重复的订单以及活动商品
        List<ShopCartItemDto> resList = new ArrayList<>();
        for (ShopCartItemDto productItem : productItems) {
            if (productItem.getDeliveryTemplateId() == null || Objects.equals(productItem.getProdType(), ProdType.PROD_TYPE_ACTIVE.value())) {
                continue;
            }
            resList.add(productItem);
        }
        return resList;
    }

    /**
     * 计算出运费模板重复的订单运费
     *
     * @param deliveryTemplateId 运费模板id
     * @param productItems       运费模板是重复的订单
     * @param userAddr           用户地址
     * @param shopIdWithShopTransFee
     * @return 运费
     */
    private double repeatListTransFee(Long deliveryTemplateId, List<ShopCartItemDto> productItems, UserAddr userAddr,
                                      Map<Long, ShopTransFeeVO> shopIdWithShopTransFee, Map<Long, TransportDto> transportMap) {
        // 商品的件数 (总的件数)
        int prodCount = 0;
        // 商品的重量
        double totalWeight = 0.0;
        // 商品的体积
        double totalVolume = 0.0;
        // 商品总金额
        double totalAmount = 0;
        for (ShopCartItemDto productItem : productItems) {
            prodCount += productItem.getProdCount();
            double itemTotalWeight = productItem.getWeight() == null ? 0 : productItem.getWeight();
            totalWeight += Arith.mul(itemTotalWeight, productItem.getProdCount());
            double itemTotalVolume = productItem.getVolume() == null ? 0 : productItem.getVolume();
            totalVolume += Arith.mul(itemTotalVolume, productItem.getProdCount());
            totalAmount = Arith.add(totalAmount, productItem.getProductTotalAmount());
        }

        // 用户所在区域id
        boolean areaIdNull = Objects.isNull(userAddr.getAreaId()) || Objects.equals(userAddr.getAreaId(), 0L);
        boolean cityIdNull = Objects.isNull(userAddr.getCityId()) || Objects.equals(userAddr.getCityId(), 0L);
        Long areaId = areaIdNull ? (cityIdNull ? userAddr.getProvinceId() : userAddr.getCityId()) : userAddr.getAreaId();
        // 找出运费模板，运费项
        Transport transport;
        if (transportMap.containsKey(deliveryTemplateId)) {
            transport = BeanUtil.map(transportMap.get(deliveryTemplateId), Transport.class);
        } else {
            transport = transportService.getTransportAndAllItems(deliveryTemplateId);
        }

        if (Objects.isNull(transport)) {
            return 0.0;
        }
        // 用户计算运费的件数
        double piece = 0.0;
        if (Objects.equals(TransportChargeType.COUNT.value(), transport.getChargeType())) {
            // 按件数计算运费
            piece = prodCount;
        } else if (Objects.equals(TransportChargeType.WEIGHT.value(), transport.getChargeType())) {
            // 按重量计算运费
            piece = totalWeight;
        } else if (Objects.equals(TransportChargeType.VOLUME.value(), transport.getChargeType())) {
            // 按体积计算运费
            piece = totalVolume;
        }

        double transFee = getTransFee(areaId, transport, piece);
        double freeTransFee = 0.0;
        // 如果有包邮条件
        if (Objects.equals(1, transport.getHasFreeCondition())) {
            List<TransfeeFree> transFeeFrees = transport.getTransfeeFrees();
            for (TransfeeFree transFeeFree : transFeeFrees) {
                List<Area> freeCityList = transFeeFree.getFreeCityList();
                for (Area area : freeCityList) {
                    if (!Objects.equals(area.getAreaId(), areaId)) {
                        continue;
                    }
                    //包邮方式 （0 满x件/重量/体积包邮 1满金额包邮 2满x件/重量/体积且满金额包邮）
                    boolean isFree = (Objects.equals(transFeeFree.getFreeType(), TransportFreeType.COUNT.value()) && piece >= transFeeFree.getPiece()) ||
                            (Objects.equals(transFeeFree.getFreeType(), TransportFreeType.AMOUNT.value()) && totalAmount >= transFeeFree.getAmount()) ||
                            (Objects.equals(transFeeFree.getFreeType(), TransportFreeType.COUNT_AND_AMOUNT.value()) && piece >= transFeeFree.getPiece() && totalAmount >= transFeeFree.getAmount());
                    if (isFree) {
                        freeTransFee = transFee;
                        transFee = 0.0;
                    }
                }
            }
        }
        Long shopId = transport.getShopId();
        // 获取指定店铺运费信息
        ShopTransFeeVO shopTransFee = getShopTransFee(shopIdWithShopTransFee, shopId);
        shopTransFee.setTransFee(Arith.add(transFee, shopTransFee.getTransFee()));
        shopTransFee.setFreeTransFee(Arith.add(freeTransFee, shopTransFee.getFreeTransFee()));
        return transFee;
    }

    private double getTransFee(Long areaId, Transport transport, double piece) {
        Transfee transFee = null;
        List<Transfee> transFees = transport.getTransfees();
        for (Transfee dbTransFee : transFees) {
            // 将该商品的运费设置为默认运费
            if (transFee == null && CollectionUtil.isEmpty(dbTransFee.getCityList())) {
                transFee = dbTransFee;
            }
            // 如果在运费模板中的城市找到该商品的运费，则将该商品由默认运费设置为该城市的运费
            boolean isContainer = dbTransFee.getCityList().stream().anyMatch(area -> area.getAreaId().equals(areaId));
            if (isContainer) {
                transFee = dbTransFee;
                break;
            }
            // 如果在运费模板中的城市找到该商品的运费，则退出整个循环
            if (transFee != null && CollectionUtil.isNotEmpty(transFee.getCityList())) {
                break;
            }
        }
        // 如果无法获取到任何运费相关信息，则返回0运费
        if (transFee == null) {
            return 0.0;
        }
        // 产品的运费
        double fee = transFee.getFirstFee();
        // 如果件数大于首件数量，则开始计算超出的运费
        if (piece > transFee.getFirstPiece()) {
            // 续件数量
            double prodContinuousPiece = Arith.sub(piece, transFee.getFirstPiece());
            // 续件数量的倍数，向上取整
            int mulNumber = (int) Math.ceil(Arith.div(prodContinuousPiece, transFee.getContinuousPiece()));
            // 续件数量运费
            double continuousFee = Arith.mul(mulNumber, transFee.getContinuousFee());
            fee = Arith.add(fee, continuousFee);
        }
        return fee;
    }


    /**
     * 计算统一运费
     * @param deliveryTemplateId
     * @param shopCartItemDtoList
     * @param shopIdWithShopTransFee
     */
    private Double calculateUnifiedFreight(Long deliveryTemplateId, List<ShopCartItemDto> shopCartItemDtoList, Map<Long, ShopTransFeeVO> shopIdWithShopTransFee) {
        double transFee = 0.0D;
        for (ShopCartItemDto shopCartItemDto : shopCartItemDtoList) {
            if (Objects.equals(shopCartItemDto.getProdType(), ProdType.PROD_TYPE_ACTIVE.value()) || Objects.equals(shopCartItemDto.getMold(), 1)) {
                // 活动商品/虚拟商品不需要计算
                continue;
            }
            Long shopId = shopCartItemDto.getShopId();
            // 必须要执行这个方法，初始化店铺模板信息（统一包邮-统一包邮只需要初始化店铺信息不需要计算运费）
            ShopTransFeeVO shopTransFee = getShopTransFee(shopIdWithShopTransFee, shopId);
            // 统一邮费的计算
            if (Objects.equals(deliveryTemplateId, DeliveryTemplateType.FREIGHT.getValue())) {
                // 统一运费只需要运费金额，跟地区、数量、重量、体积这些没有关系
                double shopCartItemTransFee = Arith.mul(shopCartItemDto.getDeliveryAmount(), shopCartItemDto.getProdCount());
                shopTransFee.setTransFee(Arith.add(shopCartItemTransFee, shopTransFee.getTransFee()));
                transFee = Arith.add(transFee, shopCartItemTransFee);
            }
        }
        return transFee;
    }

    /**
     * 获取店铺运费信息
     * @param shopIdWithShopTransFee
     * @param shopId
     * @return
     */
    private ShopTransFeeVO getShopTransFee(Map<Long, ShopTransFeeVO> shopIdWithShopTransFee, Long shopId) {
        ShopTransFeeVO shopTransFee = shopIdWithShopTransFee.get(shopId);
        if (Objects.isNull(shopTransFee)) {
            shopTransFee = new ShopTransFeeVO();
            shopTransFee.setTransFee(0.0);
            // 没有减免运费的计算
            shopTransFee.setFreeTransFee(0.0);
            shopIdWithShopTransFee.put(shopId, shopTransFee);
        }
        return shopTransFee;
    }
}
