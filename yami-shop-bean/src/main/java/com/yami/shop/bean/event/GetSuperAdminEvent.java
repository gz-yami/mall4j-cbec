package com.yami.shop.bean.event;

import lombok.Data;

/**
 * @author gaozijie
 * @since 2023-12-14
 */
@Data
public class GetSuperAdminEvent {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    public GetSuperAdminEvent(Long shopId) {
        this.shopId = shopId;
    }
}
