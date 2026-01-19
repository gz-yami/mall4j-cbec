/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.bean.enums;


import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.vo.OutletConfigInfoVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递100电子面单--快递公司类型
 * 一联 76*130
 * 电子面单账号申请：https://api.kuaidi100.com/document/zhanghaoshenqingzhinan.html
 * @author TRACK
 */
public enum DeliveryCompanyType {
    /**
     * 顺丰速运 partnerId月结账号 100*150
     */
    SHUNFENG(1, "顺丰速运", "shunfeng", "", "SFEXPRESS", "SF", "shunfeng",
            "62cfc4f9cce62b00130f0977", "61bc872bc66fb00013a1ade4"),
    /**
     * 京东快递 partnerId商家编码 100*180
     */
    JD(2, "京东快递", "jd", "", "JD", "JD", "jd",
            "60acf369f4602900133763fa", "61b700b54cb635001301fef3"),
    /**
     * 圆通速递 partnerId商家代码 partnerKey商家密钥 100*180
     */
    YUANTONG(3, "圆通速递", "yuantong", "", "YTO", "YTO", "yuantong",
            "60d1cbc4c7cbaa001441d1d9", "628c61d1e07e350013c1ae99"),
    /**
     * 韵达快递 partnerId韵达白马账号 partnerKey联调密码 100*203
     */
    YUNDA(4, "韵达快递", "yunda", "", "YUNDA", "YD", "yunda",
            "60d48922c7cbaa001441d3ab", "6114921e8b71ba00131341cf"),
    /**
     * 中通快递 partnerId合作方代码 partnerKey合作方密钥 net网点编码 100*180
     */
    ZHONGTONG(5, "中通快递", "zhongtong", "ztoOpen", "ZTO", "ZTO", "zhongtong",
            "6256a96111aeb20013c7f182", "62cd4ab8ac76350013866e6b"),
    /**
     * 申通快递 partnerId客户名称 partnerKey客户密码 net网点
     */
    SHENTONG(6, "申通快递", "shentong", "44", "STO", "STO", "shentong",
            "60d3267f71ec3d00137c8312", null),
    /**
     * 百世快递 partnerId操作编码 partnerKey密钥
     */
    HUITONGKUAIDI(7, "百世快递", "huitongkuaidi", "", "HTKY", "HTKY", "huitongkuaidi",
            "60cbff8ec7cbaa001441d087", null),
    /**
     * EMS partnerId协议客户号 partnerKey电商客户标识 100*180
     */
    EMS(8, "EMS", "ems", "", "EMS", "EMS", "ems",
            "60d57ed3c7cbaa001441d45a", "61aec09ac66fb00013a10920"),
    ;

    private final Integer type;

    private final String name;

    /**
     * 快递公司编码
     */
    private final String kuaidicom;

    /**
     * 电子面单承载编号
     */
    private final String code;
    /**
     * 物流公司编号(阿里)
     */
    private final String aliNo;
    /**
     * 物流公司编号(快递鸟)
     */
    private final String bridNo;
    /**
     * 物流公司编号(快递100)
     */
    private final String hundredNo;
    /**
     * 一联编码
     */
    private final String oneLineCode;
    /**
     * 二联编码
     */
    private final String twoLineCode;

    DeliveryCompanyType(Integer type, String name, String kuaidicom, String code, String aliNo, String bridNo, String hundredNo, String oneLineCode, String twoLineCode) {
        this.type = type;
        this.name = name;
        this.kuaidicom = kuaidicom;
        this.code = code;
        this.aliNo = aliNo;
        this.bridNo = bridNo;
        this.hundredNo = hundredNo;
        this.oneLineCode = oneLineCode;
        this.twoLineCode = twoLineCode;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getKuaidicom() {
        return kuaidicom;
    }

    public String getCode() {
        return code;
    }

    public String getOneLineCode() {
        return oneLineCode;
    }

    public String getTwoLineCode() {
        return twoLineCode;
    }

    public static DeliveryCompanyType instance(Integer type) {
        DeliveryCompanyType[] enums = values();
        for (DeliveryCompanyType statusEnum : enums) {
            if (statusEnum.getType().equals(type)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static String getTempId(Integer type, Integer paperSize) {
        DeliveryCompanyType[] enums = values();
        for (DeliveryCompanyType statusEnum : enums) {
            if (statusEnum.getType().equals(type)) {
                return paperSize == 1 ? statusEnum.getOneLineCode() : statusEnum.getTwoLineCode();
            }
        }
        return null;
    }

    public static List<OutletConfigInfoVO> getInfoList() {
        DeliveryCompanyType[] enums = values();
        List<OutletConfigInfoVO> list = new ArrayList<>(8);
        for (DeliveryCompanyType deliveryCompanyType : enums) {
            OutletConfigInfoVO outletConfigInfoVO = new OutletConfigInfoVO();
            outletConfigInfoVO.setDeliveryCompanyName(deliveryCompanyType.getName());
            outletConfigInfoVO.setDeliveryCompanyType(deliveryCompanyType.getType());
            outletConfigInfoVO.setIsConfig(0);
            list.add(outletConfigInfoVO);
        }
        return list;
    }

    public static Delivery getByDeliveryCompanyType(Long deliveryCompanyType) {
        Delivery deliveryCompanyVO = new Delivery();
        DeliveryCompanyType[] enums = values();
        for (DeliveryCompanyType statusEnum : enums) {
            if (Long.valueOf(statusEnum.getType()).equals(deliveryCompanyType)) {
//                deliveryCompanyVO.setDvyNo(statusEnum.bridNo);
                deliveryCompanyVO.setDvyNoHd(statusEnum.hundredNo);
                deliveryCompanyVO.setDvyName(statusEnum.name);
            }
        }
        return deliveryCompanyVO;
    }
}
