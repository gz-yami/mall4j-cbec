package com.yami.shop.bean.event;

import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.vo.ProductVO;
import com.yami.shop.bean.dto.TransportDto;
import com.yami.shop.bean.model.UserAddr;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lanhai
 * 判断配送区域是否超出
 */
@Data
public class CheckAddrEvent {

    public CheckAddrEvent() {
    }

    public CheckAddrEvent(UserAddr userAddr, String userId, ProductVO productVO, List<ShopCartItemDto> shopCartItemList, Integer dvyType) {
        this.userAddr = userAddr;
        this.userId = userId;
        this.productVO = productVO;
        this.shopCartItemList = shopCartItemList;
        this.dvyType = dvyType;
    }

    public CheckAddrEvent(Long addrId, String userId, ProductVO productVO, List<ShopCartItemDto> shopCartItemList, Integer dvyType) {
        this.addrId = addrId;
        this.userId = userId;
        this.productVO = productVO;
        this.shopCartItemList = shopCartItemList;
        this.dvyType = dvyType;
    }

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户地址id
     */
    private Long addrId;

    /**
     * 用户地址
     */
    private UserAddr userAddr;

    /**
     * 商品详情信息
     */
    ProductVO productVO;

    /**
     * 配送类型 1:快递 2:自提 3:无需快递 4:同城配送
     */
    private Integer dvyType;

    /**
     * 购物车商品信息列表
     */
    List<ShopCartItemDto> shopCartItemList;
    /**
     * 模板集合-查询一次就行，保存到地址中，避免重复查询数据库
     */
    Map<Long, TransportDto> transportMap;
}
