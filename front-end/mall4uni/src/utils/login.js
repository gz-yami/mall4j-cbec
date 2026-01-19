/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

const loginMethods = {
  /**
   * 登录成功
   * @param {Object} loginRes 登录成功返回的数据
   */
  loginSuccess: (loginRes) => {
    uni.setStorageSync('cbecB2cIsPrivacy', 1)
    uni.setStorageSync('cbecB2cHadLogin', true)
    uni.setStorageSync('cbecB2cToken', loginRes.accessToken)
    // 保存整个登录数据
    uni.setStorageSync('cbecB2cLoginResult', loginRes)
    // 还原全局 正在登录状态
    getApp().globalData.isLanding = false

    getApp().globalData.reloadHomePopupAd = true // 是否重新加载首页弹窗

    /**
     * 获取用户信息
     */
    http.request({
      url: '/p/user/userInfo',
      method: 'GET',
      dontTrunLogin: true
    }).then(({ data }) => {
      uni.setStorageSync('cbecB2cUserInfo', data)
      // 返回上一页处理
      util.previousPage()
      getApp().globalData.userPagesGetHotSalesProds && getApp().globalData.userPagesGetHotSalesProds()
      getApp().globalData.indexGetHotSalesProds && getApp().globalData.indexGetHotSalesProds()
    })
  },

  /**
   * 设置登录后的跳转地址
   */
  setRouteUrlAfterLogin: () => {
    const pages = getCurrentPages()
    // 登录后的回跳地址
    if (pages[pages.length - 1].route.indexOf('user-login') === -1) {
      uni.setStorageSync('cbecB2cRouteUrlAfterLogin', pages[pages.length - 1].$page.fullPath)
    }
  }
}

export default loginMethods
