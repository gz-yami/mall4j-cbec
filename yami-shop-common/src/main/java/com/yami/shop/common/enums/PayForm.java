package com.yami.shop.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付形式
 * @author gaozijie
 * @date 2023-08-10
 */
@Getter
@AllArgsConstructor
public enum PayForm {
    /**
     * 购买商品
     */
    BUY_PRODUCT(0),
    /**
     * 购买会员
     */
    BUY_VIP(1),
    /**
     * 余额充值
     */
    RECHARGE_BALANCE(2);

    private final Integer value;

    public static PayForm instance(Integer value) {
        for (PayForm payForm : PayForm.values()) {
            if (Objects.equals(payForm.getValue(), value)) {
                return payForm;
            }
        }
        return null;
    }
}
