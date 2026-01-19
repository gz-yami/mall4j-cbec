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

export const useCurrencyStore = defineStore('currency', {
  state: () => {
    return {
      // 默认货币 （全局页面主要显示的货币及结算时所使用的货币）
      defCurrency: 'USD', // 默认货币种类
      defMark: '$', // 默认货币符号

      // 需要显示换算的货币
      currency: 'USD', // 初始货币种类
      mark: '$', // 货币符号
      currencyName: 'USD - Dollar', // 初始货币名称
      exchangeRate: 1, // 汇率

      // 初始货币列表
      currencyList: [{
        currencyName: '美元',
        currencyCode: 'USD',
        symbol: '$',
        exchangeRate: 1,
        status: 1
      }]
    }
  },
  actions: {
    /**
     * 获取全局默认货币
     */
    GET_DEFAULT_CURRENCY () {
      const obj = this.currencyList.find(i => i.defaultCurrency === 1)
      if (obj) {
        this.defMark = obj.symbol || '$'
        this.defCurrency = obj.currencyCode || 'USD'
      }
    },
    /** 获取需要换算的货币种类 **/
    GET_CURRENCY () {
      this.currency = uni.getStorageSync('cbecB2cCurrency') || 'USD'
      const obj = this.currencyList.find(i => i.currencyCode === this.currency)
      this.currencyName = obj.currencyCode + ' - ' + obj.currencyName
      this.mark = obj.symbol
      this.exchangeRate = obj.exchangeRate
    },
    /** 设置并保存需要换算的货币 **/
    SET_CURRENCY (currency) {
      if (this.currency !== currency && currency) {
        uni.setStorageSync('cbecB2cCurrency', currency) // 存储
        this.GET_CURRENCY()
      }
    },
    /** 初始化货币相关数据 **/
    INIT_CURRENCY_DATA () {
      http.request({
        url: '/currency/list',
        method: 'GET'
      }).then(({ data }) => {
        if (data && data.length > 0) {
          this.currencyList = data.filter(i => i.status === 1)
          this.GET_DEFAULT_CURRENCY()
          this.GET_CURRENCY()
        }
      })
    }
  }
})
