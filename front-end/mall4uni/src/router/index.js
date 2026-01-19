/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

import uniCrazyRouter, { beforeEach, afterEach } from 'uni-crazy-router'
import { getUuid } from '../utils/flow.js'
// #ifdef H5
import { AppType } from '../utils/constant.js'
import util from '@/utils/util.js'
// #endif

let systemType = ''
// #ifdef H5
systemType = 2
// #endif
// #ifdef APP-PLUS
systemType = uni.getSystemInfoSync().platform === 'android' ? 4 : 5
// #endif
uni.setStorageSync('cbecB2cSystemType', systemType)

// 开放给main.js
export function setupRouter (app) {
  // 接收vue3的实例
  app.use(uniCrazyRouter)
  // eslint-disable-next-line no-unused-vars
  beforeEach(async (to, from, next) => {
    if (!uni.getStorageSync('cbecB2cUuid')) {
      uni.setStorageSync('cbecB2cUuid', getUuid())
    }
    next()
  })

  afterEach(async (to) => {
    // #ifdef H5
  // 微信分享
    uni.removeInterceptor('setNavigationBarTitle')
    if (uni.getStorageSync('cbecB2cAppType') !== AppType.MP) return
    // 排除不需要分享的页面（页面中已存在分享）
    const excludePathList = ['package-prod/pages/prod/prod']
    if (excludePathList.includes(to.url)) return
    const isIndex = ['pages/index/index', '*'].includes(to.url)
    Promise.resolve().then(() => {
      const uniWebConfig = useWebConfigStore().webConfig
      const imgUrl = util.checkFileUrl(uniWebConfig.uniLoginLogoImg || '')
      const title = uniWebConfig.uniTitleContent || ''
      const path = '/' + to.url + (to.search ? '?' + to.search : '')
      const sharedata = {
        title, // 分享标题
        imgUrl, // 分享图标
        link: import.meta.env.VITE_APP_H5_DOMAIN + path,
        desc: document.title || '', // 分享描述
        type: '', // 分享类型,music、video或link，不填默认为link
        dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
        dontShowLogin: true
      }
      // 如果是首页，则分享文案为欢迎语
      if (isIndex) {
        sharedata.desc = uniWebConfig.h5Welcome || ''
      }
      // 如果页面标题未设置并且不是首页，则监听页面标题设置完成后，再进行分享
      if (!sharedata.desc && !isIndex) {
        uni.addInterceptor('setNavigationBarTitle', {
          invoke ({ title }) {
            if (title) {
              sharedata.desc = title
              uni.removeInterceptor('setNavigationBarTitle')
            }
          }
        })
      }
    })
  // #endif
  })
}
