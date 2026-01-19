/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.api.adapter;

import com.yami.shop.security.common.adapter.DefaultAuthConfigAdapter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author FrozenWatermelon
 * @date 2020/7/16
 */
@Component
public class ResourceServerAdapter extends DefaultAuthConfigAdapter {

    public static final List<String> EXCLUDE_PATH = Arrays.asList(
            "/p/score/scoreLevel/listLevels",
            "/p/station/userstation",
            "/p/wx/jsapi/createJsapiSignature"

    );

    @Override
    public List<String> pathPatterns() {
        return Arrays.asList("/p/*");
    }

    @Override
    public List<String> excludePathPatterns() {
        return EXCLUDE_PATH;
    }
}
