/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.sys.multishop.aspect;


import cn.hutool.core.date.SystemClock;
import com.google.gson.Gson;
import com.yami.shop.common.util.IpHelper;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.multishop.util.SecurityUtils;
import com.yami.shop.sys.common.model.SysLog;
import com.yami.shop.sys.common.model.SysUser;
import com.yami.shop.sys.common.service.SysLogService;
import com.yami.shop.sys.common.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lgh
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class SysLogAspect {
    private final SysUserService sysUserService;
    private final SysLogService sysLogService;
    private final Gson gson;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, com.yami.shop.common.annotation.SysLog sysLog) throws Throwable {
        long beginTime = SystemClock.now();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long time = SystemClock.now() - beginTime;


        SysLog sysLogEntity = new SysLog();
        if(sysLog != null){
            //注解上的描述
            sysLogEntity.setOperation(sysLog.value());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        sysLogEntity.setMethod(className + "." + methodName + "()");
        sysLogEntity.setParams(null);
        //请求的参数
        Object[] args = joinPoint.getArgs();
        if(args != null && args.length > 0) {
            String params = null;
            if(Objects.equals(args[0].getClass().getName(), PageParam.class.getName())){
                PageParam<Object> pageParam = (PageParam<Object>) args[0];
                List<Object> records = pageParam.getRecords();
                params = gson.toJson(records);
            }else{
                params = gson.toJson(args);
            }
            sysLogEntity.setParams(params);
        }
        //设置IP地址
        sysLogEntity.setIp(IpHelper.getIpAddr());

        //用户名
        Long userId = SecurityUtils.getSysUser().getUserId();
        if (!Objects.isNull(userId)) {
            SysUser sysUser = sysUserService.getSysUserById(userId);
            if (!Objects.isNull(sysUser)) {
                sysLogEntity.setUsername(sysUser.getUsername());
            }
        }
        sysLogEntity.setTime(time);
        sysLogEntity.setCreateDate(new Date());
        //保存系统日志
        sysLogService.save(sysLogEntity);
        return result;
    }

}
