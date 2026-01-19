/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.param.UserRegisterParam;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.*;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.i18n.bean.I18nLangInfo;
import com.yami.shop.common.util.*;
import com.yami.shop.dao.UserMapper;
import com.yami.shop.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/09/11.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserCollectionService userCollectionService;
    private final ApplicationEventPublisher eventPublisher;
    private final SysConfigService sysConfigService;

    @Override
    public User getUserByUserId(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void setUserLevelBylevelId(Integer level, Integer levelType) {
        userMapper.setUserLevelBylevelId(level, levelType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserLevelByGrowth(Integer level, Integer minNeedGrowth, Integer maxNeedGrowth, Integer levelType) {
        userMapper.setMaxUserLevelBylevelId(level, minNeedGrowth, maxNeedGrowth, levelType);
    }


    /**
     * 看看有没有校验验证码成功的标识
     *
     * @param userRegisterParam
     * @param checkRegisterSmsFlag
     */
    @Override
    public void validate(UserRegisterParam userRegisterParam, String checkRegisterSmsFlag) {
        if (StrUtil.isBlank(userRegisterParam.getCheckRegisterSmsFlag())) {
            // 验证码已过期，请重新发送验证码校验
            throw new YamiShopBindException("yami.verification.expire");
        } else {
            String checkRegisterSmsFlagMobile = RedisUtil.get(checkRegisterSmsFlag);
            boolean unAuth = true;
            if (!ObjectUtils.isEmpty(userRegisterParam.getMobile())) {
                unAuth = !Objects.equals(checkRegisterSmsFlagMobile, userRegisterParam.getMobile());
            } else {
                unAuth = !Objects.equals(checkRegisterSmsFlagMobile, userRegisterParam.getMail());
            }
            if (unAuth) {
                // 验证码已过期，请重新发送验证码校验
                throw new YamiShopBindException("yami.verification.expire");
            }
        }
    }

    @Override
    public User getUserDetail(String userId) {
        return userMapper.getUserDetail(userId);
    }

    @Override
    public IPage<User> getUserPage(PageParam<User> page, User user) {
        return userMapper.getUserPage(page, user);
    }

    @Override
    public Map<String, User> getUserListByUserNumbers(Set<String> keySet) {
        if (keySet.size() == 0) {
            return new HashMap<>(16);
        }
        List<User> userList = userMapper.getUserListByUserNumbers(keySet);
        Map<String, User> userMap = userList.stream().collect(Collectors.toMap(User::getUserId, user -> user));
        return userMap;
    }

    @Override
    public CustomerRespParam countUserByParam(CustomerReqParam param) {
        CustomerRespParam respParam = new CustomerRespParam();
        Date endTime = param.getEndTime();
        Date startTime = param.getStartTime();
        // 累积粉丝数
        param.setAppId(1);
        param.setDateTime(endTime);
        if (Objects.isNull(param.getShopId())) {
            param.setMember(1);
            // 累计应该没有时间
            param.setStartTime(null);
            param.setEndTime(null);
            param.setDateTime(null);
            respParam.setFashNum(userMapper.countUserByParam(param));
            param.setDateTime(startTime);
            respParam.setPreFashNum(userMapper.countUserByParam(param));
            respParam.setFashNumRate(divRate(respParam.getPreFashNum(), respParam.getFashNum(), 4));
        }

        // 累积会员数
        param.setDateTime(null);
        param.setMember(null);
        param.setAppId(null);
        param.setDateTime(endTime);
        respParam.setMember(userMapper.countMemberByParam(param));
        param.setDateTime(startTime);
        respParam.setPreMember(userMapper.countMemberByParam(param));
        respParam.setMemberRate(divRate(respParam.getMember() - respParam.getPreMember(), respParam.getMember(), 4));

        return respParam;
    }

    @Override
    public IPage<UserManagerParam> getUserInfoPage(PageParam<User> page, UserManagerReqParam user, Integer lang) {
        // user基本信息 积分 当前余额  累计金额
        Page<UserManagerParam> pages = new Page<>();
        pages.setCurrent(page.getCurrent());
        pages.setSize(page.getSize());

        List<UserManagerParam> list = userMapper.getUserPageByParam(new PageAdapter(page), user);
        for (UserManagerParam userManagerParam : list) {
            handleUserNullValue(userManagerParam);
            userManagerParam.setConsAmount(Arith.add(userManagerParam.getConsAmount(), userManagerParam.getFreightAmount()));
            //订单实际支付金额/订单没有优惠的金额  不包含积分订单
            userManagerParam.setAverDiscount(divAverage(userManagerParam.getOrderActualAmount(), userManagerParam.getConsAmount(), 2));
            if (Objects.nonNull(userManagerParam.getUserMobile())) {
                userManagerParam.setUserMobile(PhoneUtil.hideBetween(userManagerParam.getUserMobile()).toString());
            }
            //昵称符合正则时就**
            if (PrincipalUtil.isMobile(userManagerParam.getNickName())) {
                userManagerParam.setNickName(PhoneUtil.hideBetween(userManagerParam.getNickName()).toString());
            }
            // 隐藏邮箱
            if (!ObjectUtils.isEmpty(userManagerParam.getUserMail())) {
                userManagerParam.setUserMail(HideUtil.hideMail(userManagerParam.getUserMail()));
            }
        }
        pages.setRecords(list);
        Integer total = userMapper.countGetUserPageByParam(new PageAdapter(page), user);
        pages.setTotal(total);
        return pages;
    }


    @Override
    public IPage<UserManagerParam> getUserInfoPage(Page<User> page, UserManagerReqParam user, Integer lang) {
        // user基本信息 积分 当前余额  累计金额
        Page<UserManagerParam> pages = new Page<>();
        pages.setSize(page.getSize());
        pages.setCurrent(page.getCurrent());

        List<UserManagerParam> list = userMapper.getUserPageByParam(new PageAdapter(page), user);
        for (UserManagerParam userManagerParam : list) {
            handleUserNullValue(userManagerParam);
            userManagerParam.setConsAmount(Arith.add(userManagerParam.getConsAmount(), userManagerParam.getFreightAmount()));
            //订单实际支付金额/订单没有优惠的金额  不包含积分订单
            userManagerParam.setAverDiscount(divAverage(userManagerParam.getOrderActualAmount(), userManagerParam.getConsAmount(), 2));
        }
        pages.setRecords(list);
        return pages;
    }

    private void handleUserNullValue(UserManagerParam userManagerParam) {
        if (Objects.isNull(userManagerParam.getConsAmount())) {
            userManagerParam.setConsAmount(Constant.ZERO_DOUBLE);
        }
        if (Objects.isNull(userManagerParam.getFreightAmount())) {
            userManagerParam.setFreightAmount(Constant.ZERO_DOUBLE);
        }
        if (Objects.isNull(userManagerParam.getConsTimes())) {
            userManagerParam.setConsTimes(0);
        }
        if (Objects.isNull(userManagerParam.getReduceAmount())) {
            userManagerParam.setReduceAmount(Constant.ZERO_DOUBLE);
        }
        if (Objects.isNull(userManagerParam.getAfterSaleAmount())) {
            userManagerParam.setAfterSaleAmount(Constant.ZERO_DOUBLE);
        }
        if (Objects.isNull(userManagerParam.getAfterSaleTimes())) {
            userManagerParam.setAfterSaleTimes(0);
        }
        if (Objects.isNull(userManagerParam.getSumScore())) {
            userManagerParam.setSumScore(0);
        }
        if (Objects.isNull(userManagerParam.getRechargeAmount())) {
            userManagerParam.setRechargeAmount(Constant.ZERO_DOUBLE);
        }
        if (Objects.isNull(userManagerParam.getRechargeTimes())) {
            userManagerParam.setRechargeTimes(0);
        }
    }

    @Override
    public UserManagerParam getuserInfo(String userId) {

        UserManagerParam param = userMapper.getuserInfo(userId);
        if (PrincipalUtil.isMobile(param.getNickName())) {
            param.setNickName(PhoneUtil.hideBetween(param.getNickName()).toString());
        }
        param.setCurrentScore(param.getScore());
        return param;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void destroyUser(String userId) {
        // 清空注销用户的所有数据
        userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        userCollectionService.remove(new LambdaQueryWrapper<UserCollection>().eq(UserCollection::getUserId, userId));
    }




    private Double divRate(Integer a, Integer b, Integer scale) {
        if (Objects.isNull(b) || b == 0 || Objects.isNull(a)) {
            return 0.0;
        }
        return Arith.div(a, b, scale);
    }

    private Double divAverage(Double a, Double b, Integer scale) {
        if (Objects.isNull(b) || b == 0 || Objects.isNull(a)) {
            return 0.0;
        }
        return Arith.div(a, b, scale);
    }

    private List<String> getHeadList() {
        String[] header = {
                I18nMessage.getMessage("yami.user.excel.header.seq"),
                I18nMessage.getMessage("yami.user.excel.header.mail"),
                I18nMessage.getMessage("yami.user.excel.header.pwd"),
                I18nMessage.getMessage("yami.user.excel.header.memberType"),
                I18nMessage.getMessage("yami.user.excel.header.vipEndTime"),
                I18nMessage.getMessage("yami.user.excel.header.nickName"),
                I18nMessage.getMessage("yami.user.excel.header.sex"),
                I18nMessage.getMessage("yami.user.excel.header.birth"),
                I18nMessage.getMessage("yami.user.excel.header.remark"),
                I18nMessage.getMessage("yami.user.excel.header.score"),
                I18nMessage.getMessage("yami.user.excel.header.balance"),
                I18nMessage.getMessage("yami.user.excel.header.growth")
        };
        List<String> headers = new ArrayList<>(List.of(header));

        return headers;
    }

    @Override
    public List<User> getUserByUserIds(List<String> ids) {
        return userMapper.getUserByUserIds(ids);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUsrInfo(User user, SysTypeEnum sysType) {
        updateById(user);
    }
}
