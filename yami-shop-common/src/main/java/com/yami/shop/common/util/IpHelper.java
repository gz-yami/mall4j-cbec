/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * IP帮助工具
 * @author yami
 */
@Slf4j
public class IpHelper {
    private static final String UNKNOWN = "unknown";

    public static final String LOCALHOST = "127.0.0.1";

    /**
     * 得到用户的真实地址,如果有多个就取第一个
     *
     * @return
     */
    public static String getIpAddr() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    try {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("获取用户ip错误:", e);
                    }
                }
            }
            // 通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null) {
                if (ipAddress.contains(StrUtil.COMMA)) {
                    return ipAddress.split(StrUtil.COMMA)[0];
                } else {
                    return ipAddress;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("获取用户ip错误:", e);
            return "";
        }
    }

    /**
     * 获取本地IP
     * @return
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

}
