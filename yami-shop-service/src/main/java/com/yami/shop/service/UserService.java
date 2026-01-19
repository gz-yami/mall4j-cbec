/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.param.UserRegisterParam;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.*;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.util.PageParam;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lgh on 2018/09/11.
 */
public interface UserService extends IService<User> {

    /**
     * 通过用户id获取用户信息
     * @param userId 用户id
     * @return 用户详情
     */
    User getUserByUserId(String userId);

    /**
     * 根据等级，降低对应等级用户的等级
     *
     * @param level     会员等级
     * @param levelType 会员类型
     */
    void setUserLevelBylevelId(Integer level, Integer levelType);

    /**
     * 更具成长值设置用户的等级
     * @param level 等级
     * @param minNeedGrowth 等级对应最小的成长值
     * @param maxNeedGrowth 等级对应最大的成长值
     * @param levelType 会员类型
     */
    void setUserLevelByGrowth(Integer level, Integer minNeedGrowth, Integer maxNeedGrowth, Integer levelType);

    /**
     * 根据用户id获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    User getUserDetail(String userId);

    /**
     * 校验用户注册验证码
     * @param userRegisterParam 用户注册参数
     * @param checkRegisterSmsFlag 用户注册发送短信缓存的key
     */
    void validate(UserRegisterParam userRegisterParam, String checkRegisterSmsFlag);

    /**
     * 条件查询，分页获取用户信息
     * @param page 分页参数
     * @param user 条件查询参数
     * @return 分页用户信息列表
     */
    IPage<User> getUserPage(PageParam<User> page, User user);

    /**
     * 获取用户信息map
     * @param keySet 用户id集合
     * @return 用户的map信息
     */
    Map<String, User> getUserListByUserNumbers(Set<String> keySet);

    /**
     * 统计用户信息
     * @param param 参数
     * @return 统计数据
     */
    CustomerRespParam  countUserByParam(CustomerReqParam param);

    /**
     * 条件查询，分页获取用户信息
     * @param page 分页参数
     * @param user 条件查询参数
     * @param lang 语言
     * @return 分页用户信息
     */
    IPage<UserManagerParam> getUserInfoPage(PageParam<User> page, UserManagerReqParam user,Integer lang);

    /**
     * 条件查询，自定义分页获取用户信息
     * @param page 分页参数
     * @param user 条件查询参数
     * @return 分页用户信息
     */
    IPage<UserManagerParam> getUserInfoPage(Page<User> page, UserManagerReqParam user, Integer lang);

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    UserManagerParam getuserInfo(String userId);



    /**
     *   注销账户
     * @param userId
     */
    void destroyUser(String userId);

    /**
     * 通过用户idList获取用户信息
     * @param idList 用户idList
     * @return
     */
    List<User> getUserByUserIds(List<String> idList);

    /**
     * 修改用户信息
     *
     * @param user
     * @param sysType
     */
    void updateUsrInfo(User user, SysTypeEnum sysType);
}
