/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

export const PayType = {
  SCOREPAY: 0, // 积分支付
  BALANCEPAY: 9, // 余额支付
  PAYPAL: 10, // paypal支付
  PAYCARDPAL: 12 // paypal信用卡支付
}

export const AppType = {
  MINI: 1, // 小程序
  MP: 2, // 微信公众号
  PC: 3, // pc
  H5: 4, // h5
  ANDROID: 5, // 安卓
  IOS: 6, // 苹果
  ALI: 7, // 支付宝H5
  ALIMINI: 8 // 支付宝小程序
}

// 退款单类型
export const RefundType = {
  ALL: 1, // 整单退款
  SINGLE: 2 // 单个退款
}

// 退货退款申请类型
export const RefundApplyType = {
  REFUND: 1, // 仅退款
  REFUND_AND_RETURNS: 2 // 退款退货
}

// 退款订单状态
export const ReturnMoneySts = {
  APPLY: 1, // 买家申请
  PROCESSING: 2, // 卖家接受
  CONSIGNMENT: 3, // 买家发货
  RECEIVE: 4, // 卖家收货
  SUCCESS: 5, // 退款成功
  REPEAL_APPLY: 6, // 买家撤回申请
  REJECT: 7, // 商家拒绝
  FAIL: -1 // 退款关闭
}

// 退款状态
export const RefundStatus = {
  APPLY: 1, // 申请退款
  SUCCEED: 2, // 退款成功
  PARTIAL_SUCCESS: 3, // 部分退款成功
  DISAGREE: 4, // 退款失败
  FAIL: -1 // 退款关闭
}
