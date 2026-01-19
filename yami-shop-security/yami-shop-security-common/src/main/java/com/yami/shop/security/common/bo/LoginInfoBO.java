package com.yami.shop.security.common.bo;

/**
 * 登录信息
 * @author LGH
 */
public class LoginInfoBO {

    private String bizUserId;

    /**
     * 第三方系统类型
     * @see com.yami.shop.security.common.enums.SocialType
     */
    private Integer socialType;

    public String getBizUserId() {
        return bizUserId;
    }

    public void setBizUserId(String bizUserId) {
        this.bizUserId = bizUserId;
    }

    public Integer getSocialType() {
        return socialType;
    }

    public void setSocialType(Integer socialType) {
        this.socialType = socialType;
    }

    @Override
    public String toString() {
        return "LoginInfoBO{" +
                "bizUserId='" + bizUserId + '\'' +
                ", socialType=" + socialType +
                '}';
    }
}
