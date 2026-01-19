package com.yami.shop.security.common.bo;

import com.yami.shop.common.enums.SysTypeEnum;

/**
 * 登录信息
 * @author LGH
 */
public class UidInfoBO {
    /**
     * 系统类型
     * @see SysTypeEnum
     */
    private SysTypeEnum sysType;

    /**
     * SysTypeEnum = 0时，表示c端用户的用户id
     * SysTypeEnum = 1时，表示商家的员工id
     * SysTypeEnum = 2时，表示平台的员工id
     * SysTypeEnum = 3时，表示自提点的id
     */
    private String userId;

    /**
     * 店铺id，仅当SysTypeEnum != 0时有用
     * SysTypeEnum=2时shopId固定为0
     */
    private Long shopId;

    /**
     * 是否是管理员，仅当SysTypeEnum != 0时有用
     */
    private Integer admin;

    public UidInfoBO() {
    }

    public UidInfoBO(SysTypeEnum sysType, String userId) {
        this.sysType = sysType;
        this.userId = userId;
    }

    public UidInfoBO(SysTypeEnum sysType, String userId, Long shopId) {
        this.sysType = sysType;
        this.userId = userId;
        this.shopId = shopId;
    }

    public UidInfoBO(SysTypeEnum sysType, String userId, Long shopId, Integer isAdmin) {
        this.sysType = sysType;
        this.userId = userId;
        this.shopId = shopId;
        this.admin = isAdmin;
    }

    public SysTypeEnum getSysType() {
        return sysType;
    }

    public void setSysType(SysTypeEnum sysType) {
        this.sysType = sysType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "UidInfoBO{" +
                "sysType=" + sysType +
                ", userId='" + userId + '\'' +
                ", shopId=" + shopId +
                ", isAdmin=" + admin +
                '}';
    }
}
