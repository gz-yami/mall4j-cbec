/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.config;


import java.util.Arrays;
import java.util.List;

/**
 * 常量
 * @author yami
 */
public class Constant {


    public static final String UNDERLINE = "_";


    /** 超级管理员ID */
    public static final long SUPER_ADMIN_ID = 1L;

    /**
     * 自营店id
     */
    public static final long MAIN_SHOP = 1L;

    /**
     * 如果把平台的数据也保存在店铺里面，如分类，热搜之类的，保存的店铺id
     */
    public static final long PLATFORM_SHOP_ID = 1L;
    /**
     * 平台条件标签上限
     */
    public static final long TAG_LIMIT_NUM = 20;
    /**
     * 平台店名称
     */
    public static final String PLATFORM_SHOP_NAME = "官方店";

    /**
     * 暂时的线上演示自提点id
     */
    public static final Long ON_LINE_STATION_ID = 83L;

    /**
     * 一天的秒数
     */
    public static final Integer DAY_SECOND = 24 * 60 * 60;

    /**
     * 积分名称
     */
    public static final String SCORE_CONFIG = "SCORE_CONFIG";
    public static final String SCORE_EXPLAIN = "SCORE_EXPLAIN";
    public static final String LEVEL_SHOW = "LEVEL_SHOW_%d";
    public static final String SCORE_EXPIRE = "SCORE_EXPIRE";
    public static final String SCORE_QUESTION = "SCORE_QUESTION_%d";
    /**
     * 商城缺失sku属性时的字符描述
     */
    public static final String SKU_PREFIX = "规格:";

    public static final String DEFAULT_SKU = "规格";
    /**
     * 成长值名称
     */
    public static final String GROWTH_CONFIG = "GROWTH_CONFIG";

    /** 会员初始等级id*/
    public static final int USER_LEVEL_INIT = 1;

    /** 商家线下核销*/
    public static final int SERVICE_TYPE = 1;

    /** 系统菜单最大id */
    public static final int SYS_MENU_MAX_ID = 30;

    /** 订单超时时间 */
    public static final int ORDER_MAX_TIME = 30;

    /** 订单类型：积分订单 */
    public static final int ORDER_TYPE_SCORE = 3;

    /**
     * 离即将退款超时x小时时提醒
     */
    public static final int MAX_REFUND_HOUR = 12;
    /**
     * 最大确认收货退款申请时间7天 - 退款申请
     */
    public static final int MAX_FINALLY_REFUND_TIME = 7;

    /**
     * 退款申请最大处理时间，当申请时间过了这个时间段之后，会取消退款申请 - 退款处理
     */
    public static final int MAX_REFUND_APPLY_TIME = 7;

    /**
     * 订单、分销佣金结算在确认收货后的时间，维权期过后：7（最大确认收货退款时间） + 7（退款申请最大处理时间） + 1（缓冲时间） - 订单结算，在退款处理完成之后
     */
    public static final int DISTRIBUTION_SETTLEMENT_TIME = MAX_FINALLY_REFUND_TIME + MAX_REFUND_APPLY_TIME + 1;

    /**
     * 查询订单成功状态
     */
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "error";

    /**
     * 最多可绑定十张对公户
     */
    public static final int MAX_COMPANY_CARD_NUM = 10;

    /**
     * 一级分类id
     */
    public static final Long CATEGORY_ID = 0L;

    /**
     * 配置名称
     */
    public static final String ALI_LIVE_CONFIG = "ALI_LIVE_CONFIG";
    public static final String QUICK100_CONFIG = "QUICK100_CONFIG";
    public static final String SEVENTEEN_TRACK_CONFIG = "SEVENTEEN_TRACK_CONFIG";
    public static final String ALI_QUICK_CONFIG = "ALI_QUICK_CONFIG";
    public static final String DOMAIN_CONFIG = "DOMAIN_CONFIG";
    public static final String PAYPAL_CONFIG = "PAYPAL_CONFIG";
    public static final String MERCHANT_REGISTER_PROTOCOL = "MERCHANT_REGISTER_PROTOCOL_%d";
    public static final String SHOP_PROTOCOL = "SHOP_PROTOCOL_%d";
    public static final String SERVICE_TERMS_CONFIG = "SERVICE_TERMS_CONFIG_%d";
    public static final String SERVICE_POLICY_CONFIG = "SERVICE_POLICY_CONFIG_%d";
    public static final String INTERNATIONALIZATION_CONFIG = "INTERNATIONALIZATION_CONFIG";
    public static final String WITHDRAW_CONFIG = "WITHDRAW_CONFIG";
    public static final String UPLOAD_FILE_CONFIG = "UPLOAD_FILE_CONFIG";
    public static final String MAIL_CONFIG = "MAIL_CONFIG";


    /** 汇率配置 */
    public static final String EXCHANGE_RATE_CONFIG = "EXCHANGE_RATE_CONFIG";

    /**
     * 记录缓存名称
     */
    public static final String FLOW_ANALYSIS_LOG = "flowAnalysisLog";

    /**
     * 心跳字符串
     */
    public static final String HEART_BEAT = "HEART_BEAT";


    /**
     * 最大会员等级折扣
     */
    public static final double MAX_LEVEL_DISCOUNT = 10D;

    /**
     * 最小会员等级积分倍率
     */
    public static final double MIN_LEVEL_RATE_SCORE = 1D;
    /**
     * 商品最低金额(非积分商品)
     */
    public static final Double MIN_PRODUCT_AMOUNT = 0.01D;

    /**
     * 店铺最多可以签约的分类数量
     */
    public static final int SIGNING_CATEGORY_LIMIT_NUM = 200;

    /**
     * 店铺签约的品牌数量上限
     */
    public static final int SIGNING_BRAND_LIMIT_NUM = 50;

    /**
     * 店铺可以绑定的银行卡上限
     */
    public static final int SHOP_BANK_CARD_LIMIT_NUM = 5;
    /**
     * 句号（英文符号）
     */
    public static final String PERIOD = ".";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 中文逗号
     */
    public static final String CN_COMMA = "，";
    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 星号
     */
    public static final String ASTERISK = "*";
    /**
     * 零
     */
    public static final Long ZERO_LONG = 0L;
    /**
     * 零
     */
    public static final Integer ZERO = 0;
    /**
     * 零
     */
    public static final Double ZERO_DOUBLE = 0D;
    /**
     *
     * 参考CacheKeyPrefix
     * cacheNames 与 key 之间的默认连接字符
     */
    public static final  String UNION = "::";
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 数据量大时，系统单次处理数据的数量
     */
    public static final Long MAX_DATA_HANDLE_NUM = 10000L;

    /**
     * 分销相关配置
     */
    public static final String DISTRIBUTION_CONFIG = "DISTRIBUTION_CONFIG";
    public static final String DISTRIBUTION_RECRUIT_CONFIG = "DISTRIBUTION_RECRUIT_CONFIG";
    public static final Double MAX_USER_BALANCE = 999999999.99D;
    /**
     * 解锁库存的时间， 单位：分钟
     */
    public static final int UN_LOCK_STOCK_TIME = 2;
    public static String DISTRIBUTION_REMARKS = "分销配置";
    public static String DISTRIBUTION_RECRUIT_REMARKS = "分销招募推广配置";
    public static final String PURCHASES_ORDER = "PO";

    /**
     * 签名相关
     */
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    /**
     * 系统配置相关
     */
    public static final String PAY_SWITCH_CONFIG = "PAY_SWITCH_CONFIG";

    public static final String SERVICE_SWITCH_CONFIG = "SERVICE_SWITCH_CONFIG";

    public static final String PROD_SWITCH_CONFIG = "PROD_SWITCH_CONFIG";
    /**
     * 店铺最大银行卡数量
     */
    public static final int MAX_SHOP_BANK_CARD = 5;

    /**
     * 使用商品浏览记录进行推荐时，使用的数据数量
     */
    public static final int MAX_PROD_BROWSE_NUM = 50;

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * xls文件
     */
    public static final String XLS_FILE = "xls";

    /**
     * xlsx文件
     */
    public static final String XLSX_FILE = "xlsx";

    public static final int LAST_FOUR_BY_MOBILE = 4;

    /**
     * 微信最大支付金额
     */
    public static final Double WECHAT_MAX_PAY_AMOUNT = 10000000.00D;

    public static final int MAX_NICK_NAME_LENGTH = 15;

    public static final int EXCEL_BEGIN_ROW = 2;

    /**
     * 统计的最大步骤数
     */
    public static final int MAX_ROUTE_STEP = 10;


    public static final int MAX_MYSQL_STRING_LENGTH = 255;

    /**
     * 秒杀sku缓存key
     */
    public static final String SECKILL_SKU_STOCKS_PREFIX = "SECKILL_SKU_STOCKS_";

    /**
     * 最大轮播图数量
     */
    public static final int MAX_INDEX_IMG_NUM = 10;

    public static final Integer ERROR1 = 475;

    public static final Integer ERROR2 = 476;

    public static final Integer ERROR3 = 477;

    public static final Integer STOCK_WARNING_MAX = 9999999;

    /**
     *  提货or虚拟核销码前缀
     */
    public static final String PICKUP_CODE_PREFIX = "0";

    public static final String VIRTUAL_CODE_PREFIX= "1";
    /**
     * 最小提现金额
     */
    public static final Double MIN_WITHDRAW_CASH = 1.0;

    /**
     * 最小提现次数
     */
    public static final Integer MIN_WITHDRAW_TIMES = 1;

    /**
     * 默认初始空间大小
     */
    public static final int INITIAL_CAPACITY = 16;

    public static final Long MAX_WECHA_TPAY_AMOUNT = 1000000000L;

    /**
     * aws s3预签名url有效时间(单位:秒)【默认：3分钟】
     */
    public static final Long S3_URL_DURATION_SECONDS = 180L;

    /**
     * 没有匹配的公众号消息模板列表
     */
    public static final List<Long> NO_MATCH_SEND_TYPES = Arrays.asList(
            // 催付提醒
            1L,
            // 核销提醒
            5L,
            // 拼团开团提醒
            9L,
            // 会员升级通知
            10L,
            // 退款临近超时提醒
            11L
    );

    /**
     * 允许上传的文件后缀名
     */
    public static final List<String> FILE_SUFFIX_NAMES = Arrays.asList(
            // 图片
            "gif", "jpeg", "png", "jpg", "bmp",
            // 视频
            "mp4", "mov", "wmv",
            // 其他文件
            "pdf"
    );
}
