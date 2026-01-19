package com.yami.shop.manager.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.param.OrderItemParam;
import com.yami.shop.bean.app.param.OrderParam;
import com.yami.shop.bean.enums.OrderType;
import com.yami.shop.bean.enums.ShopCityStatus;
import com.yami.shop.bean.event.CheckAddrEvent;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.bean.vo.VirtualRemarkVO;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.BasketMapper;
import com.yami.shop.service.ShopCartItemService;
import com.yami.shop.service.UserAddrService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.yami.shop.common.util.SpringContextUtils.applicationContext;

/**
 * 购物车适配器
 *
 * @author FrozenWatermelon
 */
@Slf4j
@Component
@AllArgsConstructor
public class ShopCartItemAdapter {
    private final BasketMapper basketMapper;
    private final ShopCartItemService shopCartItemService;
    private final UserAddrService userAddrService;


    /**
     * 组装获取用户提交的购物车商品项
     *
     * @param orderParam 提交订单时携带的商品信息
     * @param userId     当前用户的用户id
     * @param addrId     当前用户的用户id
     * @return 所有的商品购物项
     */
    public List<ShopCartItemDto> getShopCartItemsByOrderItems(OrderParam orderParam, String userId, Long addrId) {
        OrderItemParam orderItem = orderParam.getOrderItem();

        // 组装订单项，根据购物车或者立即购买， 分别组装订单项，购物车要查询一次数据库 shop_cart_item
        List<ShopCartItemDto> shopCartItems;
        // 当立即购买时，没有提交的订单是没有购物车信息的
        if (Objects.nonNull(orderItem)) {
            shopCartItems = new ArrayList<>(Constant.INITIAL_CAPACITY);
            log.info("立即购买-商品");
            // 立即购买只有一件商品
            ShopCartItemDto shopCartItemVO = new ShopCartItemDto(orderItem.getProdId(), orderItem.getSkuId(), orderItem.getProdCount());
            shopCartItems.add(shopCartItemVO);
            // 组装订单项数据
            shopCartItems = shopCartItemService.handleShopCartItem(shopCartItems, orderParam.getDvyTypes(), OrderType.ORDINARY);
        }
        // 从购物车提交订单
        else {
            // 查询购物车信息
            log.info("从购物车提交订单,处理购物车信息");
            shopCartItems = shopCartItemService.getCheckedShopCartItems(userId, addrId, orderParam.getDvyTypes());
        }

        return shopCartItems;

    }

    /**
     * 将参数转换成组装好的购物项
     *
     * @param orderItem        购物车物品参数
     * @param activityPriceFee 活动价
     * @return 商品购物项
     */
    public List<ShopCartItemDto> getActivityShopCartItem(OrderItemParam orderItem, List<DvyTypeDTO> dvyTypes, Double activityPriceFee, String userId) {
        ShopCartItemDto shopCartItem = new ShopCartItemDto(orderItem.getProdId(), orderItem.getSkuId(), orderItem.getProdCount());
        shopCartItem.setUserId(userId);
        shopCartItem.setPrice(activityPriceFee);
        shopCartItem.setDistributionCardNo(orderItem.getDistributionCardNo());
        // 组装订单项数据
        List<ShopCartItemDto> shopCartItemList = shopCartItemService.handleShopCartItem(Collections.singletonList(shopCartItem), dvyTypes, null);
        return shopCartItemList;
    }

    /**
     * 根据用户ID获取购物车项内容列表
     *
     * @param userId 用户ID
     * @return 购物车信息项列表
     */
    public List<ShopCartItemDto> getShopCartItems(String userId, Long addrId) {
        List<ShopCartItemDto> shopCartItemDtoList = basketMapper.getShopCartItems(userId);
        if (CollectionUtil.isEmpty(shopCartItemDtoList)) {
            return new ArrayList<>();
        }
        shopCartItemService.handleShopCartItem(shopCartItemDtoList, null, OrderType.ORDINARY);
        for (ShopCartItemDto shopCartItemDto : shopCartItemDtoList) {
            // 物流配送信息
            Product.DeliveryModeVO deliveryModeVO = Json.parseObject(shopCartItemDto.getDeliveryMode(), Product.DeliveryModeVO.class);
            shopCartItemDto.setDeliveryModeVO(deliveryModeVO);
            // 未开启同城配送
            if (!Objects.equals(ShopCityStatus.USABLE.value(), shopCartItemDto.getShopCityStatus())) {
                deliveryModeVO.setHasCityDelivery(false);
            }
            shopCartItemDto.setProductTotalAmount(Arith.mul(shopCartItemDto.getProdCount(), shopCartItemDto.getPrice()));
            shopCartItemDto.setActualTotal(shopCartItemDto.getProductTotalAmount());
            if (Objects.isNull(shopCartItemDto.getIsChecked())) {
                shopCartItemDto.setIsChecked(0);
            }
            if (StrUtil.isNotBlank(shopCartItemDto.getVirtualRemark())) {
                List<VirtualRemarkVO> virtualRemarkList = JSONUtil.parseArray(shopCartItemDto.getVirtualRemark()).toList(VirtualRemarkVO.class);
                virtualRemarkList.forEach(virtualRemark -> virtualRemark.setProdId(shopCartItemDto.getProdId()));
                shopCartItemDto.setVirtualRemarkList(virtualRemarkList);
            }
        }
        // 获取用户默认地址
        UserAddr userAddr;
        if (Objects.nonNull(addrId) && addrId > 0) {
            userAddr = userAddrService.getUserAddrByUserId(addrId, userId);
        } else {
            userAddr = userAddrService.getDefaultUserAddr(userId);
        }
        // 判断用户默认地址是否在配送区域内
        applicationContext.publishEvent(new CheckAddrEvent(userAddr, userId, null, shopCartItemDtoList, null));
        return shopCartItemDtoList;
    }
}
