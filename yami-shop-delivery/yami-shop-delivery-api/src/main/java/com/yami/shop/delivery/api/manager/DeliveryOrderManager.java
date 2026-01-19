package com.yami.shop.delivery.api.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.enums.DeliveryType;
import com.yami.shop.bean.enums.ShopCityStatus;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.bean.vo.ShopCityStatusVO;
import com.yami.shop.bean.vo.ShopTransFeeVO;
import com.yami.shop.bean.vo.UserDeliveryInfoVO;
import com.yami.shop.delivery.common.service.TransportManagerService;
import com.yami.shop.service.UserAddrService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 菠萝凤梨
 */
@Component
@AllArgsConstructor
public class DeliveryOrderManager {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryOrderManager.class);
    public static final int INITIAL_CAPACITY = 16;
    private final UserAddrService userAddrService;
    private final TransportManagerService transportManagerService;



    /**
     * 计算运费，并计算获取配送信息
     * @param userId 用户id
     * @param addrId 用户地址id
     * @param shopCartItems 购物项
     * @param dvyTypeList 订单物流信息
     * @return
     */
    public UserDeliveryInfoVO calculateAndGetDeliverInfo(String userId, Long addrId, List<ShopCartItemDto> shopCartItems, List<DvyTypeDTO> dvyTypeList) {
        UserDeliveryInfoVO userDeliveryInfoVO = new UserDeliveryInfoVO();
        userDeliveryInfoVO.setAddrId(addrId);
        userDeliveryInfoVO.setDvyTypes(dvyTypeList);
        for (ShopCartItemDto shopCartItemDto : shopCartItems) {
            shopCartItemDto.setUserId(userId);
        }
        UserAddr userAddr = userAddrService.getUserAddrByUserId(addrId, userId);
        userDeliveryInfoVO.setUserAddr(userAddr);
        if(CollectionUtil.isEmpty(shopCartItems)) {
            return userDeliveryInfoVO;
        }
        return calculateAndGetDeliverInfo(shopCartItems, userDeliveryInfoVO);
    }

    /**
     * 计算运费，并计算获取配送信息
     * @param shopCartItems 购物项
     * @param userDeliveryInfo 订单物流信息
     * @return
     */
    public UserDeliveryInfoVO calculateAndGetDeliverInfo(List<ShopCartItemDto> shopCartItems, UserDeliveryInfoVO userDeliveryInfo) {
        List<DvyTypeDTO> dvyTypes = userDeliveryInfo.getDvyTypes();
        String userId = shopCartItems.get(0).getUserId();
        userDeliveryInfo.setTotalTransFee(0.0);

        //如果是自提，查出用户常用的自提信息
        if (Objects.equals(dvyTypes.get(0).getDvyType(), DeliveryType.STATION.getValue())) {
            logger.info("自提订单判断");
            return createDeliveryInfo(userDeliveryInfo, dvyTypes, userId);
        }

        //订单的地址信息
        if (Objects.isNull(userDeliveryInfo.getUserAddr())) {
            userDeliveryInfo.setTotalFreeTransFee(0.0);
            userDeliveryInfo.setShopCityStatus(ShopCityStatus.OUT_OF_RANGE.value());
            return userDeliveryInfo;
        }
        UserAddr userAddr = userDeliveryInfo.getUserAddr();

        userDeliveryInfo.setUserAddr(userAddr);
        // 计算运费
        handleDeliveryInfo(shopCartItems, dvyTypes, userDeliveryInfo, userAddr);
        return userDeliveryInfo;
    }

    private UserDeliveryInfoVO createDeliveryInfo(UserDeliveryInfoVO userDeliveryInfo, List<DvyTypeDTO> dvyTypes, String userId) {
        Map<Long, ShopTransFeeVO> shopIdWithShopTransFee = new HashMap<>(INITIAL_CAPACITY);
        ShopTransFeeVO shopTransFeeVO = new ShopTransFeeVO();
        shopTransFeeVO.setTransFee(0.0);
        shopTransFeeVO.setFreeTransFee(0.0);
        shopTransFeeVO.setDeliveryTemplateId(0L);
        shopIdWithShopTransFee.put(dvyTypes.get(0).getShopId(), shopTransFeeVO);
        userDeliveryInfo.setShopIdWithShopTransFee(shopIdWithShopTransFee);
        userDeliveryInfo.setTotalFreeTransFee(0.0);
        return userDeliveryInfo;
    }

    private void handleDeliveryInfo(List<ShopCartItemDto> shopCartItems, List<DvyTypeDTO> dvyTypes, UserDeliveryInfoVO userDeliveryInfo, UserAddr userAddr) {
        //多个店铺总运费
        double actualTransFee = 0;
        double transFee = 0.0;
        //店铺可配送状态
        Map<Long, Integer> dvyTypeMap = dvyTypes.stream().collect(Collectors.toMap(DvyTypeDTO::getShopId, DvyTypeDTO::getDvyType));
        List<ShopCityStatusVO> shopCityStatusList = new ArrayList<>();
        Map<Long, List<ShopCartItemDto>> shopCartItemsMap = shopCartItems.stream().collect(Collectors.groupingBy(ShopCartItemDto::getShopId));
        //同城起送费
        Map<Long, Double> shopStartDeliveryFees = new HashMap<>(INITIAL_CAPACITY);

        Map<Long, ShopTransFeeVO> shopIdWithShopTransFee = new HashMap<>(INITIAL_CAPACITY);
        for (Map.Entry<Long, List<ShopCartItemDto>> shopKeyCartItemEntry : shopCartItemsMap.entrySet()) {
            transFee = 0.0;
            Long shopId = shopKeyCartItemEntry.getKey();
            if (!dvyTypeMap.containsKey(shopId)) {
                continue;
            }
            int dvyType = dvyTypeMap.get(shopId);
            ShopCityStatusVO shopCityStatusVO = new ShopCityStatusVO();
            shopCityStatusVO.setShopId(shopId);
            shopStartDeliveryFees.put(shopId, 0.0);
            if (!Objects.equals(userDeliveryInfo.getShopCityStatus(), ShopCityStatus.NO_CONFIG.value())) {
                userDeliveryInfo.setShopCityStatus(ShopCityStatus.NO_CONFIG.value());
            }
            if (Objects.equals(dvyType, DeliveryType.EXPRESS.getValue())) {
                // 快递
                transFee = transportManagerService.calculateTransFee(shopKeyCartItemEntry.getValue(), userAddr, userDeliveryInfo);
            }
//            // 如果该没有开启同城配送，则不需要判断是否在配送范围
//            shopCityStatusVO.setShopCityStatus(shopCityStatusVO.getShopCityStatus() != -2 ? transFee >= 0 ? 1 : (int) transFee : shopCityStatusVO.getShopCityStatus());
//            shopCityStatusList.add(shopCityStatusVO);
            transFee = Math.max(transFee, 0);
            actualTransFee = Math.max(transFee, 0.0);
        }

//        userDeliveryInfo.setShopCityStatusVoS(shopCityStatusList);
        userDeliveryInfo.setShopStartDeliveryFees(shopStartDeliveryFees);
        userDeliveryInfo.setTotalTransFee(actualTransFee);
        userDeliveryInfo.setShopCityStatus(0.0 <= transFee ? ShopCityStatus.USABLE.value() : Integer.parseInt(String.valueOf(transFee)));
    }
}
