package com.yami.shop.manager.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.yami.shop.bean.app.dto.*;
import com.yami.shop.bean.enums.*;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.bean.vo.*;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.constants.OrderCacheNames;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.common.util.CacheManagerUtil;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.service.CategoryService;
import com.yami.shop.service.UserAddrService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 确认订单适配
 *
 * @author FrozenWatermelon
 * @date 2020/12/07
 */
@Slf4j
@Component
@AllArgsConstructor
public class ConfirmOrderManager {

    private final CacheManagerUtil cacheManagerUtil;
    private final CategoryService categoryService;
    private final UserAddrService userAddrService;



    /**
     * 店铺营销活动计算完成后重算一次订单金额
     */
    public void recalculateAmountWhenFinishingCalculateShop(ShopCartOrderMergerDto shopCartOrderMerger, List<ShopCartDto> shopCarts, UserDeliveryInfoVO userDeliveryInfo) {
        Map<Long, ShopTransFeeVO> shopIdWithShopTransFee = userDeliveryInfo.getShopIdWithShopTransFee();
        // 所有店铺的订单信息
        List<ShopCartOrderDto> shopCartOrders = new ArrayList<>();
        double actualTotal = 0;
        double total = 0;
        int totalCount = 0;
        double allOrderReduce = 0;
        double totalTransFee = 0;
        long scoreTotal = 0;
        Map<Long, Integer> shopCityStatusMap = new HashMap<>(16);
        if(Objects.nonNull(userDeliveryInfo.getShopCityStatusVoS())){
            shopCityStatusMap = userDeliveryInfo.getShopCityStatusVoS().stream().collect(Collectors.toMap(ShopCityStatusVO::getShopId, ShopCityStatusVO::getShopCityStatus));
        }
        Map<Long, Double> shopStartDeliveryFees = new HashMap<>(16);
        if(Objects.nonNull(userDeliveryInfo.getShopStartDeliveryFees())){
            shopStartDeliveryFees = userDeliveryInfo.getShopStartDeliveryFees();
        }
        // 所有店铺所有的商品item
        for (ShopCartDto shopCart : shopCarts) {
            int shopCount = 0;
            // 每个店铺的订单信息
            ShopCartOrderDto shopCartOrder = new ShopCartOrderDto();
            shopCartOrder.setShopCityStatus(shopCityStatusMap.getOrDefault(shopCart.getShopId(), null));
            shopCartOrder.setStartDeliveryFee(shopStartDeliveryFees.containsKey(shopCart.getShopId()) ? shopStartDeliveryFees.get(shopCart.getShopId()): 0L);
            shopCartOrder.setOrderType(shopCartOrderMerger.getOrderType().value());
            shopCartOrder.setShopId(shopCart.getShopId());
            shopCartOrder.setShopName(shopCart.getShopName());
            shopCartOrder.setShopReduce(shopCart.getShopReduce());
            total = Arith.add(shopCart.getTotal(), total);
            totalCount += shopCart.getTotalCount();
            allOrderReduce = Arith.add(allOrderReduce, shopCart.getShopReduce());
            // 如果某家店因为各种优惠活动满减，使得金额变为负数，则这家店最低应该支付一分钱，而里面最贵的那件商品，也是支付一分钱，优惠金额 = （商品金额 - 一分钱）
            if (shopCart.getActualTotal() < 0.01 && !Objects.equals(shopCart.getShopId(), Constant.PLATFORM_SHOP_ID)) {
                shopCartOrder.setActualTotal(0.01);
                shopCartOrder.setShopReduce(Arith.sub(shopCart.getTotal(), 0.01));

                List<ShopCartItemDto> shopCartShopCartItem = getShopShopCartItem(shopCart.getShopCartItemDiscounts());
                resetActualTotal(shopCartShopCartItem);
            }
            //店铺优惠金额 = 优惠金额
            shopCartOrder.setShopReduce(shopCartOrder.getShopReduce());
            // 要计算运费
            totalTransFee = calculateTransFee(shopIdWithShopTransFee, totalTransFee, shopCart, shopCartOrder);
            // 店铺优惠券优惠金额
            shopCartOrder.setCouponReduce(shopCart.getCouponReduce());
            shopCartOrder.setShopComboAmount(shopCart.getComboReduce());
            shopCartOrder.setShopCartItemDiscounts(shopCart.getShopCartItemDiscounts());
            shopCartOrder.setTotal(shopCart.getTotal());
            shopCartOrder.setTotalCount(shopCart.getTotalCount());
            actualTotal = Arith.add(actualTotal, shopCartOrder.getActualTotal());
            if (Objects.equals(shopCartOrderMerger.getOrderType(), OrderType.SCORE)) {
                // 积分订单使用积分
                shopCartOrder.setUseScore(shopCart.getScoreTotal());
            } else {
                shopCartOrder.setUseScore(shopCartOrderMerger.getUsableScore());
            }
            shopCartOrder.setDiscountReduce(shopCart.getDiscountReduce());
            shopCartOrders.add(shopCartOrder);
            scoreTotal += shopCart.getScoreTotal();
        }
        shopCartOrderMerger.setActualTotal(actualTotal);
        shopCartOrderMerger.setTotal(total);
        shopCartOrderMerger.setTotalCount(totalCount);
        shopCartOrderMerger.setOrderReduce(allOrderReduce);
        shopCartOrderMerger.setTotalTransFee(totalTransFee);
        shopCartOrderMerger.setShopCartOrders(shopCartOrders);
        shopCartOrderMerger.setUserAddr(BeanUtil.map(userDeliveryInfo.getUserAddr(), UserAddrDto.class));
        shopCartOrderMerger.setShopCityStatus(userDeliveryInfo.getShopCityStatus());
        shopCartOrderMerger.setFreeTransFee(userDeliveryInfo.getTotalFreeTransFee());
        shopCartOrderMerger.setStartDeliveryFee(userDeliveryInfo.getStartDeliveryFee());
        if (Objects.equals(shopCartOrderMerger.getOrderType(), OrderType.SCORE)) {
            shopCartOrderMerger.setUsableScore(scoreTotal);
        }
    }

    private double calculateTransFee(Map<Long, ShopTransFeeVO> shopIdWithShopTransFee, double totalTransFee, ShopCartDto shopCart, ShopCartOrderDto shopCartOrder) {
        if (shopIdWithShopTransFee != null && shopIdWithShopTransFee.containsKey(shopCartOrder.getShopId())) {
            ShopTransFeeVO shopTransFeeVO = shopIdWithShopTransFee.get(shopCartOrder.getShopId());

            // 店铺的实付 = 购物车实付 + 运费
            shopCartOrder.setActualTotal(shopCart.getActualTotal() + shopTransFeeVO.getTransFee());
            // 运费
            shopCartOrder.setTransFee(shopTransFeeVO.getTransFee());
            shopCartOrder.setFreeTransFee(shopTransFeeVO.getFreeTransFee());
            totalTransFee = Arith.add(totalTransFee, shopTransFeeVO.getTransFee());
        } else {
            // 运费
            shopCartOrder.setTransFee(0.0);
            shopCartOrder.setFreeTransFee(0.0);
            // 店铺的实付 = 购物车实付 + 运费
            shopCartOrder.setActualTotal(Arith.add(shopCart.getActualTotal(), shopCartOrder.getTransFee()));
        }
        return totalTransFee;
    }

    /**
     * 获取店铺下的所有订单
     */
    private List<ShopCartItemDto> getShopShopCartItem(List<ShopCartItemDiscountDto> shopCartItemDiscounts) {
        List<ShopCartItemDto> shopCartItemList = new ArrayList<>();
        for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartItemDiscounts) {
            shopCartItemList.addAll(shopCartItemDiscount.getShopCartItems());
        }
        shopCartItemList.sort(Comparator.comparingDouble(ShopCartItemDto::getActualTotal));
        return shopCartItemList;
    }


    /**
     * 重新计算金额，避免订单无法支付
     */
    private void resetActualTotal(List<ShopCartItemDto> shopCartShopCartItem) {
        Iterator<ShopCartItemDto> iterator = shopCartShopCartItem.iterator();
        while (iterator.hasNext()) {
            ShopCartItemDto shopCartItem = iterator.next();
            shopCartItem.setShareReduce(Arith.add(shopCartItem.getShareReduce(), Objects.isNull(shopCartItem.getPlatformShareReduce()) ? 0.0 : shopCartItem.getPlatformShareReduce()));
            shopCartItem.setActualTotal(Arith.sub(shopCartItem.getProductTotalAmount(), shopCartItem.getShareReduce()));
            if (iterator.hasNext()) {
                shopCartItem.setActualTotal(0.0);
                shopCartItem.setShareReduce(shopCartItem.getProductTotalAmount());
            } else {
                shopCartItem.setActualTotal(0.01);
                shopCartItem.setShareReduce(shopCartItem.getProductTotalAmount() - 0.01);
            }
        }
    }

    /**
     * 结束平台优惠的计算之后，还要重算一遍金额
     */
    public void
    recalculateAmountWhenFinishingCalculatePlatform(ShopCartOrderMergerDto shopCartOrderMergerDto) {
        List<VirtualRemarkVO> virtualRemarkList = new ArrayList<>();

        List<ShopCartOrderDto> shopCartOrders = shopCartOrderMergerDto.getShopCartOrders();
        List<ShopCartItemDto> allShopCartItem = new ArrayList<>();
        //所有订单实际金额
        double actualTotal = 0.00;
        //所有订单优惠金额
        double allOrderReduce = 0.00;
        double totalTransFee = 0.00;
        double totalShopFreeTransFee = 0.00;
        // 计算商品数量
        int totalCount = 0;
        for (ShopCartOrderDto shopCartOrder : shopCartOrders) {
            //订单实际金额
            double orderActualTotal = 0.00;
            //商家优惠金额
            double orderReduceAmount = 0.00;
            //商家满减优惠金额
            double couponAmount = 0.00;
            //商家优惠券优惠金额
            double discountAmount = 0.00;
            //平台优惠券优惠金额
            double platformCouponAmount = 0.00;
            //平台等级优惠金额
            double levelAmount = 0.00;
            //商家优惠金额
            double orderPlatformAmount = 0.00;
            int shopCount = 0;
            // 满减金额计算
            List<ShopCartItemDiscountDto> shopCartItemDiscounts = shopCartOrder.getShopCartItemDiscounts();
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCartItemDiscounts) {
                List<ShopCartItemDto> shopCartItems = shopCartItemDiscount.getShopCartItems();
                allShopCartItem.addAll(shopCartItems);
                for (ShopCartItemDto shopCartItem : shopCartItems) {
                    shopCount += shopCartItem.getProdCount();
                    // 加上赠品的数量
                    if(CollectionUtil.isNotEmpty(shopCartItem.getGiveawayShopCartItemList())){
                        shopCount += shopCartItem.getGiveawayShopCartItemList().stream().mapToInt(ProductItemDto::getProdCount).sum();
                    }
                    // 设置默认值
                    shopCartItem.setPlatformShareReduce(null == shopCartItem.getPlatformShareReduce() ? 0.0 : shopCartItem.getPlatformShareReduce());
                    shopCartItem.setPlatformShareReduce(Arith.roundByBanker(shopCartItem.getPlatformShareReduce(), 2));
                    shopCartItem.setShareReduce(Arith.add(Arith.roundByBanker(shopCartItem.getShareReduce(), 2), shopCartItem.getPlatformShareReduce()));
                    shopCartItem.setActualTotal(Arith.sub(shopCartItem.getProductTotalAmount(), shopCartItem.getShareReduce()));
                    orderActualTotal = Arith.add(orderActualTotal, shopCartItem.getActualTotal());
                    orderReduceAmount = Arith.add(orderReduceAmount, shopCartItem.getShareReduce());
                    orderPlatformAmount = Arith.addMayBeEmpty(orderPlatformAmount, shopCartItem.getPlatformShareReduce());
                    discountAmount = Arith.addMayBeEmpty(discountAmount, shopCartItem.getDiscountAmount());
                    couponAmount = Arith.addMayBeEmpty(couponAmount, shopCartItem.getShopCouponAmount());
                    levelAmount = Arith.addMayBeEmpty(levelAmount, shopCartItem.getLevelReduce());
                    if (CollectionUtil.isNotEmpty(shopCartItem.getVirtualRemarkList())) {
                        virtualRemarkList.addAll(shopCartItem.getVirtualRemarkList());
                    }
                }
            }
            shopCartOrder.setPlatformFreeFreightAmount(null == shopCartOrder.getPlatformFreeFreightAmount() ? 0.0 : shopCartOrder.getPlatformFreeFreightAmount());
            shopCartOrder.setActualTotal(Arith.sub(Arith.add(orderActualTotal, shopCartOrder.getTransFee()), shopCartOrder.getPlatformFreeFreightAmount()));
            //放入优惠金额
            shopCartOrder.setShopReduce(orderReduceAmount);
            shopCartOrder.setCouponReduce(couponAmount);
            shopCartOrder.setDiscountReduce(discountAmount);
            shopCartOrder.setLevelReduce(levelAmount);
            shopCartOrder.setTotalCount(shopCount);
            double transFee = Arith.sub(shopCartOrder.getTransFee(),shopCartOrder.getPlatformFreeFreightAmount());
            double shopActual = Arith.add(Arith.sub(shopCartOrder.getActualTotal(),transFee),shopCartOrder.getFreeTransFee());
            boolean checkMinAmount = shopActual < 0.01
                    && (shopCartOrder.getScoreReduce() == null || shopCartOrder.getScoreReduce() < 1L);
            // 如果是折扣特别小的情况下，导致实际金额为0，改变最小支付金额为0.01元,并且优惠金额减去0.01。
            if (checkMinAmount) {
                shopCartOrder.setActualTotal(Arith.add(shopCartOrder.getActualTotal(),0.01));
                shopCartOrder.setShopReduce(Arith.sub(shopCartOrder.getShopReduce(), 0.01));
                // 并且如果等级优惠金额不为空，则在等级优惠金额减去，否则减去平台优惠券的分摊金额
                if (shopCartOrderMergerDto.getTotalLevelAmount() > 0.00) {
                    shopCartOrderMergerDto.setTotalLevelAmount(Arith.sub(shopCartOrderMergerDto.getTotalLevelAmount(), 0.01));
                    shopCartOrder.setLevelReduce(Arith.sub(shopCartOrder.getLevelReduce(), 0.01));
                    // 按照抵扣金额大小排序，扣除最大的那个商品项的等级优惠金额0.01元
                    List<ShopCartItemDto> items = allShopCartItem.stream().sorted(Comparator.comparing(ShopCartItemDto::getLevelReduce).reversed()).toList();
                    ShopCartItemDto shopCartItemDto = items.get(0);
                    shopCartItemDto.setActualTotal(0.01);
                    shopCartItemDto.setPlatformShareReduce(Arith.sub(shopCartItemDto.getPlatformShareReduce(), 0.01));
                    shopCartItemDto.setShareReduce(Arith.sub(shopCartItemDto.getShareReduce(), 0.01));
                    shopCartItemDto.setLevelReduce(Arith.sub(shopCartItemDto.getLevelReduce(),0.01));
                }
            }
            // 加上（店铺运费优惠金额+平台运费优惠金额）
            shopCartOrder.setShopReduce(Arith.add(Arith.add(shopCartOrder.getShopReduce(), shopCartOrder.getPlatformFreeFreightAmount()), shopCartOrder.getFreeTransFee()));
            actualTotal = Arith.add(actualTotal, shopCartOrder.getActualTotal());
            allOrderReduce = Arith.add(allOrderReduce, shopCartOrder.getShopReduce());
            totalTransFee = Arith.add(totalTransFee, shopCartOrder.getTransFee());
            totalShopFreeTransFee = Arith.add(totalShopFreeTransFee, shopCartOrder.getFreeTransFee());
            totalCount += shopCount;
        }

        shopCartOrderMergerDto.setTotalCount(totalCount);
        shopCartOrderMergerDto.setActualTotal(actualTotal);
        shopCartOrderMergerDto.setOrderReduce(allOrderReduce);
        shopCartOrderMergerDto.setTotalTransFee(totalTransFee);
        shopCartOrderMergerDto.setShopFreeTransFee(totalShopFreeTransFee);
        shopCartOrderMergerDto.setShopCartOrders(shopCartOrders);
        shopCartOrderMergerDto.setVirtualRemarkList(virtualRemarkList);
    }

    public void cacheCalculatedInfo(String userId, ShopCartOrderMergerDto shopCartOrderMerger) {
        // 防止重复提交
        RedisUtil.STRING_REDIS_TEMPLATE.opsForValue().set(OrderCacheNames.ORDER_CONFIRM_UUID_KEY + CacheNames.UNION + userId, String.valueOf(userId));
        // 保存订单计算结果缓存，省得重新计算 并且 用户确认的订单金额与提交的一致
        cacheManagerUtil.putCache(OrderCacheNames.ORDER_CONFIRM_KEY, String.valueOf(userId), shopCartOrderMerger);
    }

    public void reCalAmountWhenFinishCalShop(ShopCartOrderMergerDto shopCartOrderMergerDto) {
        double itemAmount;
        double orderAmount;
        double allOrderAmount = 0.0;
        double totalFreeTranFee = 0.0;
        double totalTranFee = 0.0;
        // 重新计算商品、订单、总订单的价格
        for (ShopCartOrderDto order : shopCartOrderMergerDto.getShopCartOrders()) {
            orderAmount = 0.0;
            for (ShopCartItemDiscountDto itemCoupon : order.getShopCartItemDiscounts()) {
                for (ShopCartItemDto item : itemCoupon.getShopCartItems()) {
                    item.setShopMemberAmount(0.0);
                    itemAmount = Arith.sub(item.getActualTotal(), item.getShopMemberAmount());
                    item.setShareReduce(Arith.add(item.getShareReduce(), item.getShopMemberAmount()));
                    item.setActualTotal(itemAmount);
                    orderAmount = Arith.add(orderAmount, itemAmount);
                }
            }
            orderAmount = Arith.add(orderAmount, order.getTransFee());
            order.setActualTotal(orderAmount);
            allOrderAmount = Arith.add(allOrderAmount, orderAmount);
            totalFreeTranFee = Arith.add(totalFreeTranFee, order.getFreeTransFee());
            totalTranFee = Arith.add(totalTranFee, order.getTransFee());
        }
        shopCartOrderMergerDto.setShopMemberAmount(0.0);
        if (Objects.equals(shopCartOrderMergerDto.getOrderType().value(), OrderType.ORDINARY.value())) {
            double orderShopReduce = Objects.isNull(shopCartOrderMergerDto.getOrderReduce()) ? 0.0 : shopCartOrderMergerDto.getOrderReduce();
            shopCartOrderMergerDto.setOrderShopReduce(Arith.add(orderShopReduce,shopCartOrderMergerDto.getShopMemberAmount()));
        } else {
            shopCartOrderMergerDto.setOrderShopReduce(0.0);
        }
        shopCartOrderMergerDto.setActualTotal(allOrderAmount);
        shopCartOrderMergerDto.setTotalTransFee(totalTranFee);
        shopCartOrderMergerDto.setShopFreeTransFee(totalFreeTranFee);
    }


    public UserDeliveryInfoVO getUserDeliveryInfoVO(List<ShopCartItemDto> shopCartItemsDb, String userId, List<DvyTypeDTO> dvyTypes, Long addrId) {
        UserDeliveryInfoVO userDeliveryInfoVO = new UserDeliveryInfoVO();
        setWarehouseMapByOrderItems(shopCartItemsDb, userDeliveryInfoVO);
        userDeliveryInfoVO.setAddrId(addrId);
        userDeliveryInfoVO.setDvyTypes(dvyTypes);
        DvyTypeDTO dvyTypeDTO = dvyTypes.get(0);

        // 不是自提订单，获取用户id
        if (Objects.equals(dvyTypeDTO.getDvyType(), DeliveryType.STATION.getValue())) {
            return userDeliveryInfoVO;
        }
        // 获取用户地址信息
        UserAddr userAddr = userAddrService.getUserAddrByUserId(addrId, userId);
        if (userAddr == null) {
            log.info("用户选择物流配送，获取用户地址信息为空");
            userDeliveryInfoVO.setTotalFreeTransFee(0D);
            userDeliveryInfoVO.setTotalTransFee(0D);
            userDeliveryInfoVO.setShopCityStatus(-1);
        }
        log.info("订单用户地址信息 -- userAddr:{}", userAddr);
        userDeliveryInfoVO.setUserAddr(userAddr);
        return userDeliveryInfoVO;
    }

    public Map<Long, List<WarehouseVO>> setWarehouseMapByOrderItems(List<ShopCartItemDto> shopCartItems, UserDeliveryInfoVO userDeliveryInfoVO) {
        if (MapUtil.isNotEmpty(userDeliveryInfoVO.getWarehouseMap())) {
            return userDeliveryInfoVO.getWarehouseMap();
        }
        Map<Long, List<WarehouseVO>> warehouseMap = new HashMap<>(Constant.INITIAL_CAPACITY);
        for (ShopCartItemDto shopCartItem : shopCartItems) {
            if (CollUtil.isNotEmpty(shopCartItem.getWarehouseList())) {
                warehouseMap.put(shopCartItem.getShopId(), shopCartItem.getWarehouseList());
                shopCartItem.setWarehouseList(null);
            }
            if (CollUtil.isEmpty(shopCartItem.getComboShopCartItems())) {
                continue;
            }
            for (ShopCartItemDto comboShopCartItem : shopCartItem.getComboShopCartItems()) {
                if (CollUtil.isNotEmpty(comboShopCartItem.getWarehouseList())) {
                    warehouseMap.put(comboShopCartItem.getShopId(), comboShopCartItem.getWarehouseList());
                    comboShopCartItem.setWarehouseList(null);
                }
            }
        }
        userDeliveryInfoVO.setWarehouseMap(warehouseMap);
        return warehouseMap;
    }

    public void handleShopCartStockPoint(UserDeliveryInfoVO userDeliveryInfoVO, List<ShopCartDto> shopCarts, List<ShopCartItemDto> shopCartItemsDb, Integer mold) {
        Map<Long, Integer> dvyTypeMap = userDeliveryInfoVO.getDvyTypes().stream().collect(Collectors.toMap(DvyTypeDTO::getShopId, DvyTypeDTO::getDvyType));
        List<ShopCartItemDto> shopCartItems = new ArrayList<>(Constant.INITIAL_CAPACITY);
        for (ShopCartDto shopCart : shopCarts) {
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCart.getShopCartItemDiscounts()) {
                for (ShopCartItemDto shopCartItem : shopCartItemDiscount.getShopCartItems()) {
                    if (CollUtil.isNotEmpty(shopCartItem.getComboShopCartItems())) {
                        shopCartItems.addAll(shopCartItem.getComboShopCartItems());
                    }
                    shopCartItems.add(shopCartItem);
                }
            }
        }
        Map<Long, List<WarehouseVO>> warehouseMap = setWarehouseMapByOrderItems(shopCartItemsDb, userDeliveryInfoVO);
        for (ShopCartItemDto shopCartItem : shopCartItemsDb) {
            if (CollUtil.isEmpty(shopCartItem.getGiveawayShopCartItemList()) || !Objects.equals(shopCartItem.getMold(), ProdMoldEnum.COMBO.value())) {
                continue;
            }
            shopCartItems.addAll(shopCartItem.getGiveawayShopCartItemList());
        }
        Long areaId = null;
        if (Objects.nonNull(userDeliveryInfoVO.getUserAddr())) {
            areaId = userDeliveryInfoVO.getUserAddr().getAreaId();
        }

        shopCartItems.forEach(shopCartItemVO -> shopCartItemVO.setWarehouseList(null));
    }

}
