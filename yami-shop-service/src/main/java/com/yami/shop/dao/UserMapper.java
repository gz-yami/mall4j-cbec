/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.dao;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.*;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.common.util.PageParam;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author yami
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 批量給对应等级的用户降低一个等级
     *
     * @param level     等级
     * @param levelType 会员类型
     */
    void setUserLevelBylevelId(@Param("level") Integer level, @Param("levelType") Integer levelType);

    /**
     * 获取截至日期之前的所有付费会员信息
     *
     * @param endOfDay 截至日期
     * @return
     */
    List<User> selectMemberByEndTime(@Param("endOfDay") DateTime endOfDay);

    /**
     * 根据等级，设置用户用等级
     *
     * @param level         等级
     * @param minNeedGrowth 最小需要的成长值
     * @param maxNeedGrowth 最大需要的成长值
     * @param levelType     会员类型
     */
    void setMaxUserLevelBylevelId(@Param("level") Integer level, @Param("minNeedGrowth") Integer minNeedGrowth, @Param("maxNeedGrowth") Integer maxNeedGrowth, @Param("levelType") Integer levelType);

    /**
     * 修改用户等级
     *
     * @param userList 需要更新的用户列表
     */
    void updateBatchById(@Param("userList") List<User> userList);

    /**
     * 获取用户详情
     *
     * @param userId 用户id
     * @return 用户
     */
    User getUserDetail(@Param("userId") String userId);

    /**
     * 条件按查询，分页获取用户列表
     *
     * @param page 分页参数
     * @param user 查询条件
     * @return 分页用户列表
     */
    IPage<User> getUserPage(PageParam<User> page, @Param("user") User user);

    /**
     * 获取用户id和用户注册时间
     *
     * @param userSet 用户列表
     * @return 用户列表
     */
    List<User> getUserListByUserNumbers(@Param("userSet") Set<String> userSet);

    /**
     * 根据参数，统计用户数量
     *
     * @param param 参数
     * @return 数量
     */
    Integer countUserByParam(@Param("param") CustomerReqParam param);

    /**
     * 根据参数，统计会员数量
     *
     * @param param 条件参数
     * @return 数量
     */
    Integer countMemberByParam(@Param("param") CustomerReqParam param);

    /**
     * 条件查询，分页获取会员信息列表
     *
     * @param adapter 分页参数
     * @param param   条件查询参数
     * @return 会员信息列表
     */
    List<UserManagerParam> getUserPageByParam(@Param("adapter") PageAdapter adapter,
                                              @Param("param") UserManagerReqParam param);

    /**
     * 根据参数，统计查询到的会员总数
     *
     * @param adapter 分页参数
     * @param param   条件查询参数
     * @return 数量
     */
    Integer countGetUserPageByParam(@Param("adapter") PageAdapter adapter, @Param("param") UserManagerReqParam param);

    /**
     * 获取单个用户详情
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserManagerParam getuserInfo(@Param("userId") String userId);

    /**
     * 根据多个手机号获取对应的用户
     *
     * @param phones 手机号集合
     * @return 用户列表
     */
    List<User> listByUserPhones(@Param("phones") List<String> phones);

    /**
     * 根据多个邮箱获取对应的用户
     *
     * @param mails 邮箱集合
     * @return 用户列表
     */
    List<User> listByUserMails(@Param("mails") List<String> mails);

    /**
     * 根据用户名查询用户信息(区分用户名大小写)
     *
     * @param userName
     * @return
     */
    User selectOneByUserName(@Param("userName") String userName);

    /**
     * 根据时间统计数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param levelType 等级
     * @return
     */
    List<CustomerPayParam> countUserByMemberParam(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("levelType") Integer levelType);

    /**
     * 获取所有用户id
     *
     * @return
     */
    List<String> getUserIdList();

    /**
     * 根据userIds获取用户列表
     *
     * @param ids
     * @return
     */
    List<User> getUserByUserIds(@Param("ids") List<String> ids);


    /**
     * 根据用户手机号和昵称获取用户
     *
     * @param userMobile
     * @param nickName
     * @return
     */
    String getUserByUserMobileAndNickName(@Param("userMobile") String userMobile, @Param("nickName") String nickName);
}
