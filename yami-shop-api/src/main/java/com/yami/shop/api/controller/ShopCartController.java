/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.app.dto.ShopCartDto;
import com.yami.shop.bean.app.dto.ShopCartItemDiscountDto;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.param.ChangeShopCartParam;
import com.yami.shop.bean.app.param.CheckShopCartItemParam;
import com.yami.shop.bean.dto.ShopCartExpiryItemDto;
import com.yami.shop.bean.enums.DeliveryType;
import com.yami.shop.bean.enums.ProdStatusEnums;
import com.yami.shop.bean.enums.ProdType;
import com.yami.shop.bean.model.Basket;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.model.Sku;
import com.yami.shop.bean.vo.ShopCartWithAmountVO;
import com.yami.shop.bean.vo.ShopTransFeeVO;
import com.yami.shop.bean.vo.UserDeliveryInfoVO;
import com.yami.shop.common.enums.StatusEnum;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Arith;
import com.yami.shop.delivery.api.manager.DeliveryOrderManager;
import com.yami.shop.manager.impl.ShopCartAdapter;
import com.yami.shop.manager.impl.ShopCartItemAdapter;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.BasketService;
import com.yami.shop.service.ProductService;
import com.yami.shop.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author LGH
 */
@RestController
@RequestMapping("/p/shopCart")
@Tag(name = "购物车接口")
@AllArgsConstructor
public class ShopCartController {

    private final BasketService basketService;
    private final ProductService productService;
    private final SkuService skuService;
    private final DeliveryOrderManager deliveryOrderManager;
    private final ShopCartAdapter shopCartAdapter;
    private final ShopCartItemAdapter shopCartItemAdapter;
    private final ApplicationContext applicationContext;

    @PostMapping("/info")
    @Operation(summary = "获取用户购物车信息" , description = "获取用户购物车信息，参数为用户选中的活动项数组")
    @Parameter(name = "addrId", description = "地址id")
    public ServerResponseEntity<ShopCartWithAmountVO> info(@RequestParam(value = "addrId" , required = false, defaultValue = "0") Long addrId) throws ExecutionException, InterruptedException {
        String userId = SecurityUtils.getUser().getUserId();
        // 拿到购物车的所有item
        List<ShopCartItemDto> shopCartItems = shopCartItemAdapter.getShopCartItems(userId, addrId);
        List<ShopCartItemDto> filterShopCartItems = shopCartItems.stream()
                .filter(shopCartItemDto -> Objects.equals(shopCartItemDto.getIsChecked(), 1)).collect(Collectors.toList());
        // 组合每个店铺的购物车信息
        List<ShopCartDto> shopCarts = shopCartAdapter.getShopCarts(shopCartItems);
        ShopCartWithAmountVO shopCartWithAmount = new ShopCartWithAmountVO();
        shopCartWithAmount.setShopCarts(shopCarts);
        // 购物车项，按购物id顺序倒序返回
        for (ShopCartDto shopCart : shopCarts) {
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCart.getShopCartItemDiscounts()) {
                shopCartItemDiscount.getShopCartItems().sort(Comparator.comparing(ShopCartItemDto::getBasketId).reversed());
            }
        }
        // 重新计算下运费
        List<DvyTypeDTO> dvyTypes = Collections.singletonList(new DvyTypeDTO(null, DeliveryType.EXPRESS.getValue()));
        UserDeliveryInfoVO userDeliveryInfoVO = deliveryOrderManager.calculateAndGetDeliverInfo(userId, addrId, filterShopCartItems, dvyTypes);
        shopCartWithAmount.setUserDeliveryInfo(userDeliveryInfoVO);
        this.calculateMakeUpShopCartAndAmount(shopCartWithAmount);
        return ServerResponseEntity.success(shopCartWithAmount);
    }

    private void calculateMakeUpShopCartAndAmount(ShopCartWithAmountVO shopCartWithAmount) {
        List<ShopCartDto> shopCarts = shopCartWithAmount.getShopCarts();
        int count = 0;
        double totalTransFee = 0L;
        double totalFreeTransFee = 0L;
        double totalMoney = 0.0;
        double subtractMoney = 0.0;
        double memberReduceAmount = 0.0;
        for (ShopCartDto shopCart : shopCarts) {
            double shopActualTotal = 0.0;
            double shopTotal = 0.0;
            long maxBasketId = Objects.equals(1L, shopCart.getShopId()) ? Integer.MAX_VALUE : 0L;
            for (ShopCartItemDiscountDto shopCartItemDiscount : shopCart.getShopCartItemDiscounts()) {
                for (ShopCartItemDto shopCartItem : shopCartItemDiscount.getShopCartItems()) {
                    maxBasketId = Math.max(shopCartItem.getBasketId() , maxBasketId);
                    // 选中的商品才参加总数据计算
                    if (Objects.equals(shopCartItem.getIsChecked(), 1)) {
                        shopTotal = Arith.add(shopTotal, shopCartItem.getProductTotalAmount());
                        count += shopCartItem.getProdCount();
                        totalMoney = Arith.add(totalMoney, Arith.mul(shopCartItem.getPrice(), shopCartItem.getProdCount()));
                        // 套餐商品不享受会员折扣优惠
                        boolean isCalMemberPrice = Objects.equals(shopCartItem.getUseMember(), 1)
                                && Objects.nonNull(shopCartItem.getMemberAmount())
                                && (Objects.isNull(shopCartItem.getComboId()) || shopCartItem.getComboId() == 0);
                        if (isCalMemberPrice) {
                            memberReduceAmount = Arith.add(memberReduceAmount, Arith.sub(shopCartItem.getActualTotal(), shopCartItem.getMemberTotalAmount()));
                            shopCartItem.setActualTotal(shopCartItem.getMemberTotalAmount());
                        }
                        shopActualTotal = Arith.add(shopActualTotal, shopCartItem.getActualTotal());
                    }
                }
            }
            // 计算运费
            Map<Long, ShopTransFeeVO> shopIdWithShopTransFee = null;
            if (Objects.nonNull(shopCartWithAmount.getUserDeliveryInfo())) {
                shopIdWithShopTransFee = shopCartWithAmount.getUserDeliveryInfo().getShopIdWithShopTransFee();
            }
            if (Objects.nonNull(shopIdWithShopTransFee) && shopIdWithShopTransFee.containsKey(shopCart.getShopId())) {
                ShopTransFeeVO shopTransFeeVO = shopIdWithShopTransFee.get(shopCart.getShopId());
                // 店铺的实付 = 购物车实付 + 运费
                shopCart.setActualTotal(Arith.add(shopActualTotal, shopTransFeeVO.getTransFee()));
                shopCart.setTotal(shopTotal);
                // 店铺免运费金额
                shopCart.setFreeTransFee(shopTransFeeVO.getFreeTransFee());
                // 店铺优惠金额
                shopCart.setShopReduce(shopCart.getShopReduce());
                // 运费
                shopCart.setTransFee(shopTransFeeVO.getTransFee());
            } else {
                shopCart.setTransFee(0.0);
                shopCart.setFreeTransFee(0.0);
            }
            totalFreeTransFee = Arith.add(totalFreeTransFee, shopCart.getFreeTransFee());
            totalTransFee = Arith.add(totalTransFee, shopCart.getTransFee());
            subtractMoney = Arith.add(subtractMoney,shopCart.getShopReduce());
            shopCart.setMaxBasketId(maxBasketId);
        }
        shopCarts.sort(Comparator.comparing(ShopCartDto::getMaxBasketId).reversed());
        shopCartWithAmount.setCount(count);
        shopCartWithAmount.setFreightAmount(totalTransFee);
        shopCartWithAmount.setFreeTransFee(totalFreeTransFee);
        shopCartWithAmount.setMemberReduceAmount(memberReduceAmount);
        shopCartWithAmount.setSubtractMoney(subtractMoney);
        shopCartWithAmount.setTotalMoney(totalMoney);
        shopCartWithAmount.setTotalReduceMoney(Arith.add(memberReduceAmount, subtractMoney));
        shopCartWithAmount.setFinalMoney(Arith.sub(Arith.add(shopCartWithAmount.getTotalMoney(), totalTransFee), shopCartWithAmount.getTotalReduceMoney()));
    }

    @PostMapping("/changeItem")
    @Operation(summary = "添加、修改用户购物车物品", description = "通过商品id(prodId)、skuId、店铺Id(shopId),添加/修改用户购物车商品，并传入改变的商品个数(count)，" +
            "当count为正值时，增加商品数量，当count为负值时，将减去商品的数量，当最终count值小于0时，会将商品从购物车里面删除")
    public ServerResponseEntity<Integer> changeItem(@Valid @RequestBody ChangeShopCartParam param) {
        String userId = SecurityUtils.getUser().getUserId();
        List<ShopCartItemDto> shopCartItems = shopCartItemAdapter.getShopCartItems(userId, 0L);

        Product product = productService.getProductByProdId(param.getProdId());
        Sku sku = skuService.getSkuBySkuId(param.getSkuId());
        //商品已下架
        if(Objects.equals(product.getStatus(), StatusEnum.DISABLE.value()) || Objects.equals(product.getStatus(), StatusEnum.DELETE.value())){
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.product.disable"));
        }
        // 当商品状态不正常时，不能添加到购物车
        boolean noTakeOff = !Objects.equals(product.getStatus(), ProdStatusEnums.NORMAL.getValue()) || !Objects.equals(sku.getStatus(), StatusEnum.ENABLE.value())
                // 当商品为预售商品时，不能添加到购物车
                || (Objects.nonNull(product.getPreSellStatus()) && Objects.equals(product.getPreSellStatus(), 1))
//                // 当商品为虚拟商品时，不能添加到购物车
//                || Objects.equals(product.getMold(), 1)
                // 当商品为活动商品时，不能添加到购物车
                || Objects.equals(product.getProdType(), ProdType.PROD_TYPE_ACTIVE.value());
        if (noTakeOff) {
            // 预售/活动商品不能加入购物车！
            return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.shopCart.prod.error"));
        }

        Integer skuStock = sku.getStocks();
        // 返回购物车数量
        return getBasketNum(shopCartItems, userId, param, skuStock);
    }

    private ServerResponseEntity<Integer> getBasketNum(List<ShopCartItemDto> shopCartItems, String userId, ChangeShopCartParam param,  Integer skuStock){
        Integer oldCount = 0;
        Long oldBasketId = null;
        for (ShopCartItemDto shopCartItemDto : shopCartItems) {
            if (Objects.nonNull(shopCartItemDto.getComboId()) && shopCartItemDto.getComboId() != 0) {
                continue;
            }
            if (Objects.equals(param.getSkuId(), shopCartItemDto.getSkuId())) {
                oldCount = shopCartItemDto.getProdCount();
                oldBasketId = shopCartItemDto.getBasketId();
                Basket basket = new Basket();
                basket.setUserId(userId);
                basket.setBasketCount(param.getCount() + shopCartItemDto.getProdCount());
                basket.setBasketId(shopCartItemDto.getBasketId());
                basket.setIsChecked(shopCartItemDto.getIsChecked());
                basket.setUseMember(param.getUseMember());
                basket.setDiscountId(param.getDiscountId());
                if (Objects.nonNull(param.getOldSkuId())) {
                    // 如果有个旧的sku，就说明是在切换sku
                    continue;
                }
                // 防止购物车变成负数
                if (basket.getBasketCount() <= 0) {
                    basketService.deleteShopCartItemsByBasketIds(userId, Collections.singletonList(basket.getBasketId()));
                    return ServerResponseEntity.success(basketService.getShopCartProdNum(userId));
                }
                basketService.updateShopCartItem(basket);
                return ServerResponseEntity.success(basketService.getShopCartProdNum(userId));
            }
        }

        // 切换sku
        if (Objects.nonNull(param.getOldSkuId())) {
            for (ShopCartItemDto oldShopCartItem : shopCartItems) {
                if (Objects.equals(param.getOldSkuId(), oldShopCartItem.getSkuId()) && Objects.equals(param.getBasketId(), oldShopCartItem.getBasketId())) {
                    Basket basket = new Basket();
                    basket.setUserId(userId);
                    basket.setBasketId(oldShopCartItem.getBasketId());
                    // 如果以前就存在这个商品，要把以前的商品数量累加
                    basket.setBasketCount(param.getCount() + oldCount);
                    if (skuStock < basket.getBasketCount() && param.getCount() > 0) {
                        // 商品库存不足
                        return ServerResponseEntity.showFailMsg(I18nMessage.getMessage("yami.insufficient.inventory"));
                    }
                    // 切换前和切换后的sku相同
                    if (Objects.equals(param.getOldSkuId(), param.getSkuId())){
                        return ServerResponseEntity.success();
                    }
                    basket.setSkuId(param.getSkuId());
                    if (Objects.nonNull(oldBasketId) && !Objects.equals(param.getOldSkuId(), param.getSkuId())) {
                        // 删除旧的购物项
                        basketService.deleteShopCartItemsByBasketIds(userId, Collections.singletonList(oldBasketId));
                    }
                    // 更新购物车
                    basketService.updateShopCartItem(basket);
                    return ServerResponseEntity.success(basketService.getShopCartProdNum(userId));
                }
            }
        }
        // 所有都正常时
        if (skuStock > 0) {
            basketService.addShopCartItem(param, userId);
        }
        // 添加成功
        return ServerResponseEntity.success(basketService.getShopCartProdNum(userId));
    }

    @PostMapping("/checkItems")
    @Operation(summary = "勾选购物车")
    public ServerResponseEntity<Void> checkItems(@Valid @RequestBody List<CheckShopCartItemParam> params) {
        if (CollectionUtil.isEmpty(params)) {
            return ServerResponseEntity.success();
        }
        String userId = SecurityUtils.getUser().getUserId();
        basketService.checkShopCartItems(userId, params);
        return ServerResponseEntity.success();
    }

    @GetMapping("/prodCount")
    @Operation(summary = "获取购物车商品数量" , description = "获取所有购物车商品数量")
    public ServerResponseEntity<Integer> prodCount() {
        String userId = SecurityUtils.getUser().getUserId();
        return ServerResponseEntity.success(basketService.getShopCartProdNum(userId));
    }

    @GetMapping("/expiryProdList")
    @Operation(summary = "获取购物车失效商品信息" , description = "获取购物车失效商品列表")
    public ServerResponseEntity<List<ShopCartExpiryItemDto>> expiryProdList() {
        String userId = SecurityUtils.getUser().getUserId();
        List<ShopCartItemDto> shopCartItems = basketService.getShopCartExpiryItems(userId);
        //根据店铺ID划分item
        Map<Long, List<ShopCartItemDto>> shopCartItemDtoMap = shopCartItems.stream().collect(Collectors.groupingBy(ShopCartItemDto::getShopId));

        // 返回一个店铺对应的所有信息
        List<ShopCartExpiryItemDto> shopCartExpiryItems = Lists.newArrayList();

        for (Long key : shopCartItemDtoMap.keySet()) {
            ShopCartExpiryItemDto shopCartExpiryItemDto = new ShopCartExpiryItemDto();
            shopCartExpiryItemDto.setShopId(key);
            List<ShopCartItemDto> shopCartItemList = Lists.newArrayList();
            for (ShopCartItemDto tempShopCartItemDto : shopCartItemDtoMap.get(key)) {
                shopCartExpiryItemDto.setShopName(tempShopCartItemDto.getShopName());
                shopCartItemList.add(tempShopCartItemDto);
            }
            shopCartExpiryItemDto.setShopCartItemDtoList(shopCartItemList);
            shopCartExpiryItems.add(shopCartExpiryItemDto);
        }
        return ServerResponseEntity.success(shopCartExpiryItems);
    }

    @DeleteMapping("/cleanExpiryProdList")
    @Operation(summary = "清空用户失效商品" , description = "清空用户失效商品")
    public ServerResponseEntity<Void> cleanExpiryProdList() {
        String userId = SecurityUtils.getUser().getUserId();
        basketService.cleanExpiryProdList(userId);
        return ServerResponseEntity.success();
    }

    @PutMapping("/deleteItem")
    @Operation(summary = "删除用户购物车物品" , description = "通过购物车id删除用户购物车物品")
    public ServerResponseEntity<Void> deleteItem(@RequestBody List<Long> basketIds) {
        String userId = SecurityUtils.getUser().getUserId();
        basketService.deleteShopCartItemsByBasketIds(userId, basketIds);
        return ServerResponseEntity.success();
    }

    @DeleteMapping("/deleteAll")
    @Operation(summary = "清空用户购物车所有物品" , description = "清空用户购物车所有物品(暂无用)")
    public ServerResponseEntity<String> deleteAll() {
        String userId = SecurityUtils.getUser().getUserId();
        basketService.deleteAllShopCartItems(userId);
        // 删除成功
        return ServerResponseEntity.success(I18nMessage.getMessage("yami.delete.successfully"));
    }
}
