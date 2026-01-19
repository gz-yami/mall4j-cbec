/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

import { defineStore } from 'pinia'
import Cookie from 'vue-cookies'

export const useCurrencyStore = defineStore('currency', {
  state: () => {
    return {
      currencyObj: {
        currencyName: '美元',
        currencyCode: 'USD',
        symbol: '$',
        exchangeRate: 1,
        status: 1
      }, // 初始货币对象
      // 默认货币 （全局页面主要显示的货币及结算时所使用的货币）
      defCurrency: Cookie.get('cbecB2cCurrency')?.currencyCode || 'USD', // 默认货币种类
      defMark: Cookie.get('cbecB2cCurrency')?.symbol || '$' // 默认货币符号
    }
  },
  actions: {
    /**
     * 获取全局默认货币
     */
    GET_DEFAULT_CURRENCY () {
      this.defCurrency = this.currencyObj.currencyCode
      this.defMark = this.currencyObj.symbol
    },
    /** 更新并保存全局默认货币 **/
    UPDATE_CURRENCY (currency) {
      if (currency && this.defCurrency !== currency) {
        this.INIT_CURRENCY_DATA()
      }
    },
    /** 初始化货币相关数据 **/
    async INIT_CURRENCY_DATA () {
      // 未登录无法获取列表
      if (!Cookie.get('cbecB2cAuthorization_vp')) return
      http({
        url: http.adornUrl('/platform/currency/getDefaultCurrency'),
        method: 'get'
      })
        .then(({ data }) => {
          this.currencyObj = data
          Cookie.set('cbecB2cCurrency', data) // 永久存储
          this.GET_DEFAULT_CURRENCY()
        })
    }
  }
})
