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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.model.NoticeTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告用户标签关联信息
 * @author LGH
 */
public interface NoticeTagMapper extends BaseMapper<NoticeTag> {


    /**
     * 通过用户获取用户可以看到的公告ids
     *
     * @param userId
     * @return
     */
    List<Long> listNoticeIdByUserId(@Param("userId") String userId);
}
