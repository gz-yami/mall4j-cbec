package com.yami.shop.delivery.api.listener;

import com.yami.shop.bean.app.dto.DvyTypeDTO;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.vo.ProductVO;
import com.yami.shop.bean.enums.DeliveryType;
import com.yami.shop.bean.event.CalculateTransfeeEvent;
import com.yami.shop.bean.vo.UserDeliveryInfoVO;
import com.yami.shop.common.util.BeanUtil;
import com.yami.shop.delivery.api.manager.DeliveryOrderManager;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author: zsm
 * @date: 2023/3/22 11:24
 */
@Component("calculateTransfeeListener")
@AllArgsConstructor
public class CalculateTransfeeListener {
    private final DeliveryOrderManager deliveryOrderManager;

    /**
     * 计算商品运费
     */
    @EventListener(CalculateTransfeeEvent.class)
    public void checkAddrListenerEvent(CalculateTransfeeEvent event) {
        String userId = event.getUserId();
        Long addrId = event.getAddrId();
        ProductVO productVO = event.getProductVO();
        ShopCartItemDto shopCartItemDto = BeanUtil.map(productVO, ShopCartItemDto.class);
        shopCartItemDto.setProdCount(1);
        shopCartItemDto.setDeliveryAmount(shopCartItemDto.getDeliveryAmount() == null ? 0.0 : shopCartItemDto.getDeliveryAmount());
        shopCartItemDto.setProductTotalAmount(productVO.getPrice());
        UserDeliveryInfoVO userDeliveryInfoVO = deliveryOrderManager.calculateAndGetDeliverInfo(userId, addrId, Collections.singletonList(shopCartItemDto), Collections.singletonList(new DvyTypeDTO(productVO.getShopId(), DeliveryType.EXPRESS.getValue())));
        productVO.setUserDeliveryInfo(userDeliveryInfoVO);
    }
}
