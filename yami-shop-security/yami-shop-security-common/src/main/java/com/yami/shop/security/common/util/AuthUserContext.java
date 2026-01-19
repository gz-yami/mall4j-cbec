/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.util;


import com.alibaba.ttl.TransmittableThreadLocal;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.security.common.bo.UidInfoBO;

import java.util.Objects;

/**
 * @author FrozenWatermelon
 * @date 2020/7/16
 */
public class AuthUserContext {

    private static final ThreadLocal<UidInfoBO> UID_HOLDER = new TransmittableThreadLocal<>();

    public static UidInfoBO get() {
        return UID_HOLDER.get();
    }

    public static void set(String uid) {
        UID_HOLDER.set(getUidInfo(uid));
    }

    public static void clean() {
        if (UID_HOLDER.get() != null) {
            UID_HOLDER.remove();
        }
    }

    public static void set(UidInfoBO uidInfoBo) {
        UID_HOLDER.set(uidInfoBo);
    }

    /**
     * 获取系统类型
     * @return
     */
    public static Integer getSysType() {
        return get().getSysType().value();
    }

    /**
     * 获取userId(用户端)
     * @return
     */
    public static String getUserId() {
        return get().getUserId();
    }

    /**
     * 获取商家端EmployeeId
     * @return
     */
    public static Long getEmployeeId() {
        return Long.parseLong(getUserId());
    }

    /**
     * 获取平台端系统用户id
     * @return
     */
    public static Long getSysUserId() {
        return Long.parseLong(getUserId());
    }

    /**
     * 获取店铺Id
     * @return
     */
    public static Long getShopId() {
        return Constant.PLATFORM_SHOP_ID;
    }

    /**
     * 获取自提点Id
     * @return
     */
    public static Long getStationId() {
        return Long.parseLong(getUserId());
    }

    public static String getUid() {
        return getUid(get());
    }

    public static Boolean getIsAdmin() {
        return get().getAdmin() == 1;
    }

    /**
     * 生成各系统唯一uid
     * @param uidInfoBo uid含有的信息
     * @return
     */
    public static String getUid(UidInfoBO uidInfoBo) {

        StringBuilder sb = new StringBuilder();
        // uidInfoArray[0]
        sb.append(uidInfoBo.getSysType().value())
                // uidInfoArray[1]
                .append(Constant.UNDERLINE).append(uidInfoBo.getUserId());

        if (Objects.equals(uidInfoBo.getSysType(), SysTypeEnum.ORDINARY)) {
            return sb.toString();
        }
        // uidInfoArray[2]
        sb.append(Constant.UNDERLINE).append(uidInfoBo.getShopId())
                // uidInfoArray[3]
                .append(Constant.UNDERLINE).append(uidInfoBo.getAdmin());
        return sb.toString();
    }

    public static UidInfoBO getUidInfo(String uid) {
        String[] uidInfoArray = uid.split(Constant.UNDERLINE);
        UidInfoBO uidInfoBO = new UidInfoBO(SysTypeEnum.instance(Integer.valueOf(uidInfoArray[0])), uidInfoArray[1]);

        if (Objects.equals(uidInfoBO.getSysType(), SysTypeEnum.ORDINARY)) {
            return uidInfoBO;
        }
        uidInfoBO.setShopId(Long.valueOf(uidInfoArray[2]));
        uidInfoBO.setAdmin(Integer.valueOf(uidInfoArray[3]));
        return uidInfoBO;
    }
}
